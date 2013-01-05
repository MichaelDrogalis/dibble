(ns dibble.postgres
  (:require [korma.core :refer [exec-raw]]
            [korma.db :refer [create-db postgres default-connection]]
            [clj-time.core :refer [date-time]]
            [dibble.vendor :refer [connect describe-table]]))

(def smallint-regex  #"smallint")
(def integer-regex   #"integer")
(def bigint-regex    #"bigint")
(def real-regex      #"real")
(def double-regex    #"double precision")
(def decimal-regex   #"(decimal|numeric)")
(def char-regex      #"(char|character|character varying|varchar)\((\d+)\)")
(def text-regex      #"text")
(def bytea-regex     #"bytea")
(def timestamp-regex #"timestamp without time zone")
(def date-regex      #"date")

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

(defn real-metadata [column description]
  {(keyword column)
   {:type :decimal
    :min -9999
    :max 9999}})

(defn double-metadata [column description]
  {(keyword column)
   {:type :decimal
    :min -9999999999999
    :max 9999999999999}})

(defn decimal-metadata [column description]
  {(keyword column)
   {:type :decimal
    :min -1e65
    :max 1e65}})

(defn char-metadata [column description]
  {(keyword column)
   {:type :string
    :max-chars (read-string (nth description 2))}})

(defn text-metadata [column description]
  {(keyword column)
   {:type :string}})

(defn bytea-metadata [column description]
  {(keyword column)
   {:type :binary
    :min 0
    :max 255}})

(defn timestamp-metadata [column description]
  {(keyword column)
   {:type :datetime
    :min (date-time -4713471)
    :max (date-time 294276)}})

(defn date-metadata [column description]
  {(keyword column)
   {:type :datetime
    :min (date-time -4713)
    :max (date-time 5874897)}})

(defn postgres-to-clj-type [[column data-type]]
  (first
   (filter
    identity
    (map
     (fn [[regex metadata-fn]]
       (if-let [description (re-matches regex data-type)]
         (metadata-fn column description)))
     [[smallint-regex  smallint-metadata]
      [integer-regex   integer-metadata]
      [bigint-regex    bigint-metadata]
      [real-regex      real-metadata]
      [double-regex    double-metadata]
      [decimal-regex   decimal-metadata]
      [char-regex      char-metadata]
      [text-regex      text-metadata]
      [bytea-regex     bytea-metadata]
      [timestamp-regex timestamp-metadata]
      [date-regex      date-metadata]]))))

(def make-connection
  (memoize (fn [spec] (create-db (postgres spec)))))

(defmethod connect :postgres [{:keys [database]}]
  (default-connection (make-connection database)))

(defmethod describe-table :postgres [args]
  (let [query (exec-raw (str "select column_name, data_type from INFORMATION_SCHEMA.COLUMNS where table_name='" (name (:table args)) "'") :results)
        fields (map :column_name query)
        types (map :data_type query)]
    (apply merge (map postgres-to-clj-type (partition 2 (interleave fields types))))))

