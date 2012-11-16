(ns dibble.sqlite3
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))

(def char-regex      #"char\((\d+)\)")
(def varchar-regex   #"varchar\((\d+)\)")
(def tinyint-regex   #"tinyint")
(def smallint-regex  #"smallint")
(def integer-regex   #"(integer|int)")
(def mediumint-regex #"mediumint")
(def bigint-regex    #"bigint")

(defn char-metadata [column description]
  {(keyword column) {:type :string :max-chars (read-string (nth description 1))}})

(defn varchar-metadata [column description]
  {(keyword column) {:type :string :max-chars (read-string (nth description 1))}})

(defn tinyint-metadata [column description]
  {(keyword column) {:type :integer :min -128 :max 127}})

(defn smallint-metadata [column description]
  {(keyword column) {:type :integer :min -32768 :max 32767}})

(defn integer-metadata [column description]
  {(keyword column) {:type :integer :min -2147483648 :max 2147483647}})

(defn mediumint-metadata [column description]
  {(keyword column) {:type :integer :min -8388608 :max 8388607}})

(defn bigint-metadata [column description]
  {(keyword column) {:type :integer :min -9223372036854775808 :max 9223372036854775807}})

(defn sqlite3-to-clj-type [[column data-type]]
  (first
   (filter
    identity
    (map
     (fn [[regex metadata-fn]]
       (if-let [description (re-matches regex data-type)]
         (metadata-fn column description)))
     [[char-regex      char-metadata]
      [varchar-regex   varchar-metadata]
      [tinyint-regex   tinyint-metadata]
      [smallint-regex  smallint-metadata]
      [integer-regex   integer-metadata]
      [mediumint-regex mediumint-metadata]
      [bigint-regex    bigint-metadata]]))))

(def connect-to-db (memoize #(default-connection (create-db (sqlite3 %)))))

(defn sqlite3-db [args]
  (connect-to-db (:database args))
  (let [query (exec-raw (str "PRAGMA table_info([" (name (:table args)) "]);") :results)
        fields (map :name query)
        types (map :type query)]
    (apply merge (map sqlite3-to-clj-type (partition 2 (interleave fields types))))))

