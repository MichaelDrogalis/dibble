(ns dibble.mysql
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))

(defn mysql-to-clj-type [[column data-type]]
  (cond (re-matches #"varchar.*" data-type) {(keyword column) :string}
        (re-matches #"int.*" data-type) {(keyword column) :integer}))

(defn mysql-db [args]
  (default-connection (create-db (mysql (:database args))))
  (let [query (exec-raw (str "show columns from " (name (:table args))) :results)
        fields (map :Field query)
        types (map :Type query)]
    (apply merge (map mysql-to-clj-type (partition 2 (interleave fields types))))))
