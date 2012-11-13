(ns dibble.mysql
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))

(def varchar-regex #"varchar\((\d+)\)")
(def tinyint-regex #"tinyint.*")
(def integer-regex #"int.*")
(def float-regex   #"float(\((\d+),(\d+)\))?")
(def double-regex  #"double(\((\d+),(\d+)\))?")
(def decimal-regex #"decimal(\((\d+),(\d+)\))?")

(defn varchar-metadata [column description]
  {(keyword column) {:type :string :max-chars (read-string (nth description 1))}})

(defn tinyint-metadata [column description]
  {(keyword column) {:type :integer :min -128 :max 127}})

(defn integer-metadata [column description]
  {(keyword column) {:type :integer :min -2147483648 :max 2147483647}})

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

(defn mysql-to-clj-type [[column data-type]]
  (first
   (filter
    identity
    (map
     (fn [[regex metadata-fn]]
       (if-let [description (re-matches regex data-type)]
         (metadata-fn column description)))
     [[varchar-regex varchar-metadata]
      [tinyint-regex tinyint-metadata]
      [integer-regex integer-metadata]
      [float-regex float-metadata]
      [double-regex double-metadata]
      [decimal-regex decimal-metadata]]))))

(def connect-to-db (memoize #(default-connection (create-db (mysql %)))))

(defn mysql-db [args]
  (connect-to-db (:database args))
  (let [query (exec-raw (str "show columns from " (name (:table args))) :results)
        fields (map :Field query)
        types (map :Type query)]
    (apply merge (map mysql-to-clj-type (partition 2 (interleave fields types))))))
