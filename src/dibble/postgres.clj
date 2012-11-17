(ns dibble.postgres
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))

(def smallint-regex #"smallint")
(def integer-regex  #"integer")
(def bigint-regex   #"bigint")

(defn smallint-metadata [column description]
  {(keyword column)
   {:type :integer
    :min -32768
    :max 32767}})

(defn integer-metadata [column description]
  {(keyword column)
   {:type :integer
    :min -2147483648
    :max 2147483647}})

(defn bigint-metadata [column description]
  {(keyword column)
   {:type :integer
    :min -9223372036854775808
    :max 9223372036854775807}})

(defn postgres-to-clj-type [[column data-type]]
  (first
   (filter
    identity
    (map
     (fn [[regex metadata-fn]]
       (if-let [description (re-matches regex data-type)]
         (metadata-fn column description)))
     [[smallint-regex smallint-metadata]
      [integer-regex  integer-metadata]
      [bigint-regex   bigint-metadata]]))))

(def connect-to-db (memoize #(default-connection (create-db (postgres %)))))

(defn postgres-db [args]
  (connect-to-db (:database args))
  (let [query (exec-raw (str "select column_name, data_type from INFORMATION_SCHEMA.COLUMNS where table_name='" (name (:table args)) "'") :results)
        fields (map :column_name query)
        types (map :data_type query)]
    (partition 2 (interleave fields types))))

