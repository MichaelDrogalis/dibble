(ns dibble.sqlite3
  (:require [korma.core :refer :all]
            [korma.db :refer :all]))

(def char-regex       #"char\((\d+)\)")
(def varchar-regex #"varchar\((\d+)\)")

(defn char-metadata [column description]
  {(keyword column) {:type :string :max-chars (read-string (nth description 1))}})

(defn varchar-metadata [column description]
  {(keyword column) {:type :string :max-chars (read-string (nth description 1))}})

(defn sqlite3-to-clj-type [[column data-type]]
  (first
   (filter
    identity
    (map
     (fn [[regex metadata-fn]]
       (if-let [description (re-matches regex data-type)]
         (metadata-fn column description)))
     [[char-regex    char-metadata]
      [varchar-regex varchar-metadata]]))))

(def connect-to-db (memoize #(default-connection (create-db (sqlite3 %)))))

(defn sqlite3-db [args]
  (connect-to-db (:database args))
  (let [query (exec-raw (str "PRAGMA table_info([" (name (:table args)) "]);") :results)
        fields (map :name query)
        types (map :type query)]
    (apply merge (map sqlite3-to-clj-type (partition 2 (interleave fields types))))))

