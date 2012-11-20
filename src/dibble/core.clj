(ns dibble.core
  (:require [korma.core :refer :all]
            [korma.db :refer :all]
            [dibble.mysql :as mysql]
            [dibble.postgres :as postgres]
            [dibble.sqlite3 :as sqlite3]
            [dibble.strings :refer :all]
            [dibble.numbers :refer :all]
            [dibble.time :refer :all]
            [dibble.binary :refer :all]))

(declare seed-table)

(defmacro defseed [seed-name args & rules]
  `(def ~seed-name [~args ~@rules]))

(defn parse-description [args]
  (cond (= (:vendor (:database args)) :mysql) (mysql/mysql-db args)
        (= (:vendor (:database args)) :postgres) (postgres/postgres-db args)
        (= (:vendor (:database args)) :sqlite3) (sqlite3/sqlite3-db args)
        :else (throw (Throwable. "Database :vendor not supported"))))

(defn clean-table [table]
  (delete table))

(defn apply-policies [args]
  (let [tables (conj (:dependents args) (:table args))]
    (cond (= (:policy args) :clean-slate) (dorun (map clean-table tables)))))

(defn apply-external-policies [args]
  (if-not (empty? (:external-dependents args))
    (dorun
     (map
      (fn [dependent]
        (parse-description dependent)
        (delete (:table dependent)))
      (:external-dependents args)))))

(defn bequeath-value! [args data]
  (when (:fk args)
    (dorun (map
            (fn [[foreign-table foreign-column]]
              (apply seed-table (concat [(assoc (first foreign-table) :autogen {foreign-column data})]
                                        (rest foreign-table))))
            (:fk args)))))

(defn seed-table
  ([bundled-args] (apply seed-table bundled-args))
  ([args & seeds]
     (let [table-description (parse-description args)]
       (apply-policies args)
       (apply-external-policies args)
       (dotimes [_ (:n args 1)]
         (let [generated-data (map (fn [f] (f args table-description)) seeds)
               seed-data (apply merge (map :seeds generated-data))
               fk-data (map :fks generated-data)]
           (parse-description args)
           (insert (:table args) (values seed-data))
           (dorun (map #(apply bequeath-value! %) fk-data)))))))

(defn- dispatch-type [constraints args]
  (let [data-type (:type constraints)]
    (cond (= data-type :string) (randomized-string constraints args)
          (= data-type :integer) (randomized-integer constraints args)
          (= data-type :decimal) (randomized-decimal constraints args)
          (= data-type :datetime) (randomized-datetime constraints args)
          (= data-type :binary) (randomized-blob constraints args))))

(defn select-value [column args f]
  (partial
   (fn [column args table-args table-description]
     (let [result (f column args table-description table-args)]
       {:seeds {column result} :fks [args result]}))
   column args))

(defn randomized
  ([column] (randomized column {}))
  ([column args]
     (select-value column args
                   (fn [column args table]
                     (dispatch-type (get table column) args)))))

(defn inherit
  ([column] (inherit column {}))
  ([column args]
     (select-value column args
                   (fn [column _ table]
                     (get (:autogen table) column)))))

(defn with-fn
  ([column f] (with-fn column f {}))
  ([column f args]
     (select-value column args (fn [_ _ _] (f)))))

(defn value-of
  ([column value] (value-of column value {}))
  ([column value args]
     (select-value column args (constantly value))))

