(ns dibble.mysql
  (:require [korma.core :refer :all]
            [korma.db :refer :all]
            [clj-time.core :as time]))

(def char-regex       #"char\((\d+)\)")
(def varchar-regex    #"varchar\((\d+)\)")
(def tinytext-regex   #"tinytext")
(def text-regex       #"text")
(def mediumtext-regex #"mediumtext")
(def longtext-regex   #"longtext")
(def tinyint-regex    #"tinyint.*")
(def smallint-regex   #"smallint.*")
(def integer-regex    #"int.*")
(def mediumint-regex  #"mediumint.*")
(def bigint-regex     #"bigint.*")
(def float-regex      #"float(\((\d+),(\d+)\))?")
(def double-regex     #"double(\((\d+),(\d+)\))?")
(def decimal-regex    #"decimal(\((\d+),(\d+)\))?")
(def timestamp-regex  #"timestamp")
(def datetime-regex   #"datetime")
(def date-regex       #"date")
(def time-regex       #"time")
(def tinyblob-regex   #"tinyblob")
(def blob-regex       #"blob")
(def mediumblob-regex #"mediumblob")
(def longblob-regex   #"longblob")

(defn char-metadata [column description]
  {(keyword column) {:type :string :min 0 :max (read-string (nth description 1))}})

(defn varchar-metadata [column description]
  {(keyword column) {:type :string :min 0 :max (read-string (nth description 1))}})

(defn tinytext-metadata [column description]
  {(keyword column) {:type :string :min 0 :max 255}})

(defn text-metadata [column description]
  {(keyword column) {:type :string :min 0 :max 65535}})

(defn mediumtext-metadata [column description]
  {(keyword column) {:type :string :min 0 :max 16777215}})

(defn longtext-metadata [column description]
  {(keyword column) {:type :string :min 0 :max 4294967295}})

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

(defn float-max-value [regex-result n max]
  (if-not (nil? (nth regex-result n))
    (dec (Math/pow 10 (- (read-string (nth regex-result 2)) 2)))
    (dec max)))

(defn float-metadata [column description]
  (let [max 1e38
        integral-max (float-max-value description 2 max)]
    {(keyword column)
     {:type :decimal
      :min (* -1 integral-max)
      :max integral-max}}))

(defn double-metadata [column description]
  (let [max 1.7976931348623157E+307
        integral-max (float-max-value description 2 max)]
    {(keyword column)
     {:type :decimal
      :min (* -1 integral-max)
      :max integral-max}}))

(defn decimal-metadata [column description]
  (let [max 1e65
        integral-max (float-max-value description 2 max)]
    {(keyword column)
     {:type :decimal
      :min (* -1 integral-max)
      :max integral-max}}))

(defn timestamp-metadata [column description]
  {(keyword column)
   {:type :datetime
    :min (time/date-time 1970 1 1 0 0 1)
    :max (time/date-time 2038 1 19 3 14 7)}})

(defn datetime-metadata [column description]
  {(keyword column)
   {:type :datetime
    :min (time/date-time 1000 1 1 0 0 0)
    :max (time/date-time 9999 12 31 23 59 59)}})

(defn date-metadata [column description]
  {(keyword column)
   {:type :datetime
    :min (time/date-time 1000 1 1)
    :max (time/date-time 9999 12 31)}})

(defn time-metadata [column description]
  {(keyword column)
   {:type :datetime
    :min (time/date-time 1000 1 1 0 0 0)
    :max (time/date-time 9999 1 1 23 59 59)}})

(defn tinyblob-metadata [column description]
  {(keyword column)
   {:type :binary
    :min 0
    :max 255}})

(defn blob-metadata [column description]
  {(keyword column)
   {:type :binary
    :min 0
    :max 511}})

(defn mediumblob-metadata [column description]
  {(keyword column)
   {:type :binary
    :min 0
    :max 1023}})

(defn longblob-metadata [column description]
  {(keyword column)
   {:type :binary
    :min 0
    :max 2047}})  

(defn mysql-to-clj-type [[column data-type]]
  (first
   (filter
    identity
    (map
     (fn [[regex metadata-fn]]
       (if-let [description (re-matches regex data-type)]
         (metadata-fn column description)))
     [[char-regex       char-metadata]
      [varchar-regex    varchar-metadata]
      [tinytext-regex   tinytext-metadata]
      [text-regex       text-metadata]
      [mediumtext-regex mediumtext-metadata]
      [longtext-regex   longtext-metadata]
      [tinyint-regex    tinyint-metadata]
      [smallint-regex   smallint-metadata]
      [integer-regex    integer-metadata]
      [mediumint-regex  mediumint-metadata]
      [bigint-regex     bigint-metadata]
      [float-regex      float-metadata]
      [double-regex     double-metadata]
      [decimal-regex    decimal-metadata]
      [timestamp-regex  timestamp-metadata]
      [datetime-regex   datetime-metadata]
      [date-regex       date-metadata]
      [time-regex       time-metadata]
      [tinyblob-regex   tinyblob-metadata]
      [blob-regex       blob-metadata]
      [mediumblob-regex mediumblob-metadata]
      [longblob-regex   longblob-metadata]]))))

(def connect-to-db (memoize #(default-connection (create-db (mysql %)))))

(defn mysql-db [args]
  (connect-to-db (:database args))
  (let [query (exec-raw (str "show columns from " (name (:table args))) :results)
        fields (map :Field query)
        types (map :Type query)]
    (apply merge (map mysql-to-clj-type (partition 2 (interleave fields types))))))

