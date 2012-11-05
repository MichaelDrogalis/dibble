(ns dibble.mysql
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))

(defn mysql-to-clj-type [[column data-type]]
  (if-let [description (re-matches #"varchar\((\d+)\)" data-type)]
    {(keyword column) {:type :string :max-chars (read-string (nth description 1))}}
    (if-let [description (re-matches #"int.*" data-type)]
      {(keyword column) {:type :integer :bytes 4}}
      (if-let [description (re-matches #"(double|decimal)\((\d+),(\d+)\)" data-type)]
        {(keyword column) {:type :decimal :accuracy (read-string (nth description 2)) :precision (read-string (nth description 3))}}
        (if (re-matches #"float" data-type)
          {(keyword column) {:type :float}})))))

(defn mysql-db [args]
  (default-connection (create-db (mysql (:database args))))
  (let [query (exec-raw (str "show columns from " (name (:table args))) :results)
        fields (map :Field query)
        types (map :Type query)]
    (apply merge (map mysql-to-clj-type (partition 2 (interleave fields types))))))
