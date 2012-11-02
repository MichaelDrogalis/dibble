(ns dibble.core
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))

(defn mysql-to-clj-type [[column data-type]]
  (cond (re-matches #"varchar.*" data-type) {(keyword column) :string}
        (re-matches #"int.*" data-type) {(keyword column) :integer}))

(defn mysql-db [args]
  (defdb target-db (mysql (:database args)))
  (let [query (exec-raw (str "show columns from " (name (:table args))) :results)
        fields (map :Field query)
        types (map :Type query)]
    (apply merge (map mysql-to-clj-type (partition 2 (interleave fields types))))))

(defn randomized [column]
  (partial
   (fn [column table-description]
     (let [data-type (get table-description column)]
       (cond (= data-type :string) {column "Mike"}
             (= data-type :integer) {column 42})))
   column))

(defn seed [& rules]
  (fn [table-description]
    (reduce
     (fn [data-seed rule-fn]
       (merge data-seed (rule-fn table-description)))
     {} rules)))

(defn seed-table [args & seeds]
  (let [table-description (cond (= (:vendor (:database args)) :mysql) (mysql-db args)
                                :else (throw (Throwable. "Database :vendor not supported")))]
    (map (fn [f] (f table-description)) seeds)))

(defn -main [& args]
  (seed-table
   {:database {:vendor :mysql :db "simulation" :user "root" :password ""} :table :people}
   (seed
    (randomized :name)
    (randomized :number))
   (seed
    (randomized :name))))
