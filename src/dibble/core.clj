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

(defn table-description [{:keys [database] :as args}]
  (let [vendor (:vendor database)]
    (cond (= vendor :mysql)    (mysql/mysql-db args)
          (= vendor :postgres) (postgres/postgres-db args)
          (= vendor :sqlite3)  (sqlite3/sqlite3-db args)
          :else (throw (Throwable. (str "Database :vendor " vendor " not supported"))))))

(defn make-connection [{:keys [database] :as args}]
  (let [vendor (:vendor database)]
    (cond (= vendor :mysql)    (mysql/connect-to-db database)
          (= vendor :postgres) (postgres/connect-to-db database)
          (= vendor :sqlite3)  (sqlite3/connect-to-db database)
          :else (throw (Throwable. (str "Database :vendor " vendor " not supported"))))))

(defmacro with-connection [args & exprs]
  `(let [previous-connection# default-connection]
     (make-connection ~args)
     ~@exprs
     (default-connection previous-connection#)))

(defn clean-table [table]
  (delete table))

(defn apply-policies [args]
  (let [tables (conj (:dependents args) (:table args))]
    (cond (= (:policy args) :clean-slate) (doall (map clean-table tables)))))

(defn apply-external-policies [args]
  (if-not (empty? (:external-dependents args))
    (doall
     (map
      (fn [dependent]
        (with-connection args
          (clean-table (:table dependent))))
      (:external-dependents args)))))

(defn bequeath-value! [args data]
  (when (:fk args)
    (do (map
         (fn [[foreign-table foreign-column]]
           (apply seed-table (concat [(assoc (first foreign-table) :autogen {foreign-column data})]
                                     (rest foreign-table))))
         (:fk args)))))

(defn seed-table
  ([bundled-args] (apply seed-table bundled-args))
  ([args & seeds]
     (with-connection args
       (let [table-structure (table-description args)]
         (apply-policies args)
         (apply-external-policies args)
         (dotimes [_ (:n args 1)]
           (let [generated-data (map (fn [f] (f args table-structure)) seeds)
                 seed-data (apply merge (map :seeds generated-data))
                 fk-data (map :fks generated-data)]
             (insert (:table args) (values seed-data))
             (dorun (map #(apply bequeath-value! %) fk-data))))))))

(defn dispatch-type [constraints args]
  (let [data-type (:type constraints)
        f (cond (= data-type :string)   randomized-string
                (= data-type :integer)  randomized-integer
                (= data-type :decimal)  randomized-decimal
                (= data-type :datetime) randomized-datetime
                (= data-type :binary)   randomized-blob)]
    (f constraints args)))

(defn select-value [column options f]
  (partial
   (fn [column options table-args table-structure]
     (let [result (f column options table-structure table-args)]
       (bequeath-value! options result)
       {:seeds {column result} :fks [options result]}))
   column options))

(defn randomized
  ([column & {:as options}]
     (select-value
      column options
      (fn [column options table-structure _]
        (dispatch-type (get table-structure column) options)))))

(defn inherit
  ([column & {:as options}]
     (select-value
      column options
      (fn [column _ table-structure _]
        (get (:autogen table-structure) column)))))

(defn with-fn
  ([column f & {:as options}]
     (select-value column options (fn [_ _ _ _] (f)))))  

(defn value-of
  ([column value & {:as options}]
     (select-value column options (constantly value))))

(seed-table
 {:database {:db "simulation" :user "root" :password "" :vendor :mysql}
  :table :people :policy :clean-slate :n 10}
 (randomized :name :length 16))

