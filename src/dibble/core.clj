(ns dibble.core
  (:require [korma.core :refer :all]
            [korma.db :refer :all]
            [dibble.mysql :as mysql]
            [dibble.strings :refer :all]
            [dibble.numbers :refer :all]))

(defmacro defseed [seed-name args & rules]
  `(def ~seed-name [~args ~@rules]))

(defn seed [& rules]
  (fn [table-description]
    (reduce
     (fn [data-seed rule-fn]
       (merge data-seed (rule-fn table-description)))
     {} rules)))

(defn parse-description [args]
  (cond (= (:vendor (:database args)) :mysql) (mysql/mysql-db args)
        :else (throw (Throwable. "Database :vendor not supported"))))

(defn clean-table [table]
  (delete table))

(defn apply-policies [args]
  (let [tables (concat [(:table args)] (:dependents args))]
    (cond (= (:policy args) :clean-slate) (dorun (map clean-table tables)))))

(defn seed-table
  ([bundled-args] (apply seed-table bundled-args))
  ([args & seeds]
     (let [table-description (parse-description args)]
       (apply-policies args)
       (dotimes [_ (:n args 1)]
         (let [data (apply merge (map (fn [f] (f args table-description)) seeds))]
           (insert (:table args) (values data)))))))

(defn bequeath-value! [args data]
  (if (:fk args)
    (dorun
     (map
      (fn [[foreign-table foreign-column]]
        (apply seed-table (concat [(assoc (first foreign-table) :autogen {foreign-column data})]
                                  (rest foreign-table))))
      (:fk args)))))

(defn randomized
  ([column] (randomized column {}))
  ([column args]
     (partial
      (fn [column args seeding-args table-description]
        (let [constraints (get table-description column)
              data-type (:type constraints)
              result (cond (= data-type :string) (randomized-string constraints args)
                           (= data-type :integer) (randomized-integer constraints args)
                           (= data-type :decimal) (randomized-double constraints args)
                           (= data-type :float) (randomized-float constraints args))]
          (bequeath-value! args result)
          {column result}))
      column args)))

(defn inherit
  ([column] (inherit column {}))
  ([column args]
     (partial
      (fn [column args seeding-args table-description]
        (let [result (get (:autogen seeding-args) column)]
          (bequeath-value! args result)
          {column result}))
      column args)))

(defn value-of
  ([column value]
     (partial
      (fn [column value seeding-args table-description]
        {column value})
      column value)))

(def db {:vendor :mysql :db "simulation" :user "root" :password ""})

(defseed people
  {:database db :table :persons :policy :clean-slate :n 5}
  (randomized :name {:min 3 :max 8})
  (randomized :about {:min 5 :max 15})
  (randomized :number)
  (randomized :secret)
  (randomized :salt {:min 5.0 :max 8.75}))

(seed-table people)

