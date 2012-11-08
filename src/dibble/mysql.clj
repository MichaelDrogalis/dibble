(ns dibble.mysql
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))

(defn mysql-to-clj-type [[column data-type]]
  (if-let [description (re-matches #"varchar\((\d+)\)" data-type)]
    {(keyword column) {:type :string :max-chars (read-string (nth description 1))}}
    (if-let [description (re-matches #"int.*" data-type)]
      {(keyword column) {:type :integer :bytes 4}})))

(def connect-to-db (memoize #(default-connection (create-db (mysql %)))))

(defn mysql-db [args]
  (connect-to-db (:database args))
  (let [query (exec-raw (str "show columns from " (name (:table args))) :results)
        fields (map :Field query)
        types (map :Type query)]
    (apply merge (map mysql-to-clj-type (partition 2 (interleave fields types))))))
