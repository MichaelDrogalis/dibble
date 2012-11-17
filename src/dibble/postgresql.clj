(ns dibble.postgresql
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))

(def connect-to-db (memoize #(default-connection (create-db (postgres %)))))

(defn postgresql-db [args]
  (connect-to-db (:database args))
  (let [query (exec-raw (str "select column_name, data_type from INFORMATION_SCHEMA.COLUMNS where table_name='" (name (:table args)) "'") :results)
        fields (map :column_name query)
        types (map :data_type query)]
    (partition 2 (interleave fields types))))

(println (postgresql-db
 {:database {:db "postgres" :user "postgres" :password "mike" :vendor :postgres} :table :people :policy :clean-slate :n 5}))

