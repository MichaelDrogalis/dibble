(ns dibble.sqlite3
  (:require [korma.core :refer :all]
            [korma.db :refer :all]
            [dibble.mysql :as mysql]))

(def make-connection
  (memoize (fn [spec] (create-db (sqlite3 spec)))))

(defn connect-to-db [db-spec]
  (default-connection (make-connection db-spec)))

(defn sqlite3-to-clj-type [type-data]
  (mysql/mysql-to-clj-type type-data))

(defn sqlite3-db [args]
  (let [query (exec-raw (str "PRAGMA table_info([" (name (:table args)) "]);") :results)
        fields (map :name query)
        types (map :type query)]
    (merge (apply merge (map sqlite3-to-clj-type (partition 2 (interleave fields types)))) (:types args))))

