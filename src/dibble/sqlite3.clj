(ns dibble.sqlite3
  (:require [korma.core :refer [exec-raw]]
            [korma.db :refer [create-db sqlite3 default-connection]]
            [dibble.mysql :as mysql]
            [dibble.vendor :refer [connect describe-table]]))

(defn sqlite3-to-clj-type [type-data]
  (mysql/mysql-to-clj-type type-data))

(def make-connection
  (memoize (fn [spec] (create-db (sqlite3 spec)))))

(defmethod connect :sqlite3 [db-spec]
  (default-connection (make-connection db-spec)))

(defmethod describe-table :sqlite3 [args]
  (let [query (exec-raw (str "PRAGMA table_info([" (name (:table args)) "]);") :results)
        fields (map :name query)
        types (map :type query)]
    (merge (apply merge (map sqlite3-to-clj-type (partition 2 (interleave fields types)))) (:types args))))

