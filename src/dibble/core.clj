(ns dibble.core
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))

(defn randomized [column-name]
  5)

(defmacro seed [& columns])

(defn mysql-to-clj-type [[column data-type]]
  (cond (re-matches #"varchar.*" data-type) [(keyword column) :string]
        (re-matches #"int.*" data-type) [(keyword column) :integer]))

(defn mysql-db [args]
  (let [query (str "show columns from " (name (:table args)))]
    (defdb target-db (mysql (:database args)))
    (apply hash-map
           (flatten
            (map mysql-to-clj-type
                 (apply hash-map (interleave
                                  (map :Field (exec-raw query :results))
                                  (map :Type (exec-raw query :results)))))))))

(defn seed-table [args]
  (cond (= (:vendor (:database args)) :mysql) (mysql-db args)
        :else (println "Database :vendor not supported")))

(seed-table
 {:database {:vendor :mysql :db "simulation" :user "root" :password ""}
  :table :people})

(defn -main [& args])