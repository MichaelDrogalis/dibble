(ns dibble.core
  (:require [korma.core :refer :all]
            [korma.db :refer :all]
            [zombie.core :refer :all]
            [dibble.mysql :as mysql]))

(defn simple-random-floating []
  (+ (* 10 (rand)) (rand)))
  
(defn randomized [column]
  (partial
   (fn [column table-description]
     (let [data-type (:type (get table-description column))]
       (cond (= data-type :string) {column (random-string)}
             (= data-type :integer) {column (int (random-integer))}
             (= data-type :decimal) {column (simple-random-floating)}
             (= data-type :float) {column (simple-random-floating)})))
   column))

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
  (let [table-description (parse-description args)
        data (map (fn [f] (f table-description)) seeds)]
    (println data)
    (apply-policies args)
    (insert (:table args) (values data))))

(defn -main [& args]
  (seed-table
   {:database {:vendor :mysql :db "simulation" :user "root" :password ""} :table :persons :policy :clean-slate}
   (seed
    (randomized :name)
    (randomized :number)
    (randomized :money)
    (randomized :about)
    (randomized :secret)
    (randomized :salt))))