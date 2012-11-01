(ns dibble.core
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))

(defn mysql-to-clj-type [[column data-type]]
  (cond (re-matches #"varchar.*" data-type) {(keyword column) :string}
        (re-matches #"int.*" data-type) {(keyword column) :integer}))

(defn mysql-db [args]
  (let [query (str "show columns from " (name (:table args)))]
    (defdb target-db (mysql (:database args)))
    (apply merge
           (map mysql-to-clj-type
                (partition 2 2
                           (interleave
                            (map :Field (exec-raw query :results))
                            (map :Type (exec-raw query :results))))))))

(defn seed-table [args]
  (cond (= (:vendor (:database args)) :mysql) (mysql-db args)
        :else (println "Database :vendor not supported")))

(def description (seed-table {:database {:vendor :mysql :db "simulation" :user "root" :password ""} :table :people}))

(defn randomized [column table-description]
  (let [data-type (get table-description column)]
    (cond (= data-type :string) "Mike"
          (= data-type :integer) 42)))

(randomized :name description)
(randomized :number description)

(defn -main [& args])