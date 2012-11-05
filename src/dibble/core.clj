(ns dibble.core
  (:require [korma.core :refer :all]
            [korma.db :refer :all]
            [dibble.mysql :as mysql]
            [dibble.strings :refer :all]))

(defn random-integer [low high]
  (first (shuffle (range low (inc high)))))

(defn randomized-string [column]
  (random-string (random-integer 0 (:max-chars column))))

(defn randomized-integer [column]
  (Integer/parseInt (apply str (map (fn [_] (first (shuffle (range 0 2)))) (range 0 (dec (* (:bytes column) 8))))) 2))

(defn randomized [column]
  (partial
   (fn [column table-description]
     (let [data-type (:type (get table-description column))]
       (cond (= data-type :string) {column (randomized-string (get table-description column))}
             (= data-type :integer) {column (randomized-integer (get table-description column))}
             (= data-type :decimal) {column 0.00}
             (= data-type :float) {column 0.00})))
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
    (randomized :number))))