(ns dibble.mysql
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))

(def varchar-regex #"varchar\((\d+)\)")
(def integer-regex #"int.*")

(defn varchar-metadata [column description]
  {(keyword column) {:type :string :max-chars (read-string (nth description 1))}})

(defn integer-metadata [column description]
  {(keyword column) {:type :integer :bytes 4}})  

(defn mysql-to-clj-type [[column data-type]]
  (first
   (filter
    identity
    (map
     (fn [[regex metadata-fn]]
       (if-let [description (re-matches regex data-type)]
         (metadata-fn column description)))
     [[varchar-regex varchar-metadata]
      [integer-regex integer-metadata]]))))

(def connect-to-db (memoize #(default-connection (create-db (mysql %)))))

(defn mysql-db [args]
  (connect-to-db (:database args))
  (let [query (exec-raw (str "show columns from " (name (:table args))) :results)
        fields (map :Field query)
        types (map :Type query)]
    (apply merge (map mysql-to-clj-type (partition 2 (interleave fields types))))))
