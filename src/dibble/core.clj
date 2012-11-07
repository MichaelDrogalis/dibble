(ns dibble.core
  (:require [korma.core :refer :all]
            [korma.db :refer :all]
            [dibble.mysql :as mysql]
            [dibble.strings :refer :all]
            [dibble.numbers :refer :all]))

(defn seed [& rules]
  (fn [table-description]
    (reduce
     (fn [data-seed rule-fn]
       (merge data-seed (rule-fn table-description)))
     {} rules)))

(defn parse-description [args]
  (cond (= (:vendor (:database args)) :mysql) (mysql/mysql-db args)
        :else (throw (Throwable. "Database :vendor not supported"))))

(defn apply-policies [args]
  (if (= (:policy args) :clean-slate)
    (delete (:table args))))
  
(defn seed-table [args & seeds]
  (let [table-description (parse-description args)]
    (apply-policies args)
    (dotimes [_ (:n args 1)]
      (let [data (apply merge (map (fn [f] (f args table-description)) seeds))]
        (insert (:table args) (values data))))))

(defn randomized
  ([column] (randomized column {}))
  ([column args]
     (partial
      (fn [column args seeding-args table-description]
        (let [constraints (get table-description column)
              data-type (:type constraints)
              result (cond (= data-type :string) (randomized-string constraints args)
                           (= data-type :integer) (randomized-integer constraints args))]
          (if (:fk args)
            (do
              (let [[foreign-table foreign-column] (:fk args)]
                (apply seed-table (concat [(assoc (first foreign-table) :autogen {foreign-column result})] (rest foreign-table))))))
          
          {column result}))
      column args)))

(defmacro defseed [seed-name args & rules]
  `(def ~seed-name [~args ~@rules]))

(def db {:vendor :mysql :db "simulation" :user "root" :password ""})

(defn inherit
  ([column] (inherit column {}))
  ([column args]
     (partial
      (fn [column args seeding-args table-description]
        {column (get (:autogen seeding-args) column)})
      column args)))

(defseed pets
  {:database db :table :pets}
  (inherit :pid)
  (randomized :name))

(defseed people
  {:database db :table :persons :policy :clean-slate :n 50}
  (randomized :name)
  (randomized :number {:fk [pets :pid]}))

(defn -main [& args]
  (apply seed-table people))

