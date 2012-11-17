(ns dibble.sqlite3
  (:require [korma.core :refer :all]
            [korma.db :refer :all]
            [dibble.mysql :as mysql]))

(def connect-to-db (memoize #(default-connection (create-db (sqlite3 %)))))

(defn sqlite3-to-clj-type [type-data]
  (mysql/mysql-to-clj-type type-data))

(defn sqlite3-db [args]
  (connect-to-db (:database args))
  (let [query (exec-raw (str "PRAGMA table_info([" (name (:table args)) "]);") :results)
        fields (map :name query)
        types (map :type query)]
    (apply merge (map sqlite3-to-clj-type (partition 2 (interleave fields types))))))

