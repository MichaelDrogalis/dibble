(ns dibble.sqlite3
  (:require [korma.core :refer [exec-raw]]
            [korma.db :refer [create-db sqlite3 default-connection]]
            [dibble.mysql :as mysql]
            [dibble.vendor :refer [connect describe-table]]))

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

(defmethod connect :sqlite3  [args] (connect-to-db (:database args)))
(defmethod describe-table :sqlite3  [args] (sqlite3-db args))

