(ns dibble.core
  (:require [korma.core :refer [insert delete values]]
            [korma.db :refer [default-connection]]
            [dibble.mysql :as mysql]
            [dibble.postgres :as postgres]
            [dibble.sqlite3 :as sqlite3]
            [dibble.random :refer :all]))

(declare seed-table)

(defmacro defseed [seed-name args & rules]
  `(def ~seed-name [~args ~@rules]))

(defprotocol PersistentConnection
  (connect [this args])
  (describe-table [this args]))

(defrecord MySQL []
  PersistentConnection
  (connect [_ args] (mysql/connect-to-db (:database args)))
  (describe-table [_ args] (mysql/mysql-db args)))

(defrecord Postgres []
  PersistentConnection
  (connect [_ args] (postgres/connect-to-db (:database args)))
  (describe-table [_ args] (postgres/postgres-db args)))

(defrecord SQLite3 []
  PersistentConnection
  (connect [_ args] (sqlite3/connect-to-db (:database args)))
  (describe-table [_ args] (sqlite3/sqlite3-db args)))

(defn vendor [{:keys [database]}]
  (let [vendor (:vendor database)]
    (cond (= vendor :mysql) (MySQL.)
          (= vendor :postgres (Postgres.))
          (= vendor :sqlite3 (SQLite3.)))))

(defmacro with-connection [vendor-record args & exprs]
  `(let [args# ~args
         previous-connection# default-connection]
     (connect ~vendor-record args#)
     ~@exprs
     (default-connection previous-connection#)))

(defn clean-table! [table]
  (delete table))

(defn apply-policies! [args]
  (let [tables (conj (:dependents args) (:table args))]
    (cond (= (:policy args) :clean-slate) (doall (map clean-table! tables)))))

(defn apply-external-policies! [args]
  (if-not (empty? (:external-dependents args))
    (doall (map (fn [dependent]
                  (with-connection args
                    (clean-table! (:table dependent))))
                (:external-dependents args)))))

(defn bequeath-value! [args data]
  (when (:fk args)
    (do (map
         (fn [[foreign-table foreign-column]]
           (apply seed-table (concat [(assoc (first foreign-table) :autogen {foreign-column data})]
                                     (rest foreign-table))))
         (:fk args)))))

(defn insert-data! [args rows table-structure]
  (let [generation (map (fn [f] (f args table-structure)) rows)
        seeds (apply merge (map :seeds generation))
        fks (map :fks generation)]
    (insert (:table args) (values seeds))
    (dorun (map #(apply bequeath-value! %) fks))))

(defn seed-table
  ([bundled-args] (apply seed-table bundled-args))
  ([args & rows]
     (let [vendor-record (vendor args)]
       (with-connection vendor-record args
         (let [table-structure (describe-table vendor-record args)]
           (apply-policies! args)
           (apply-external-policies! args)
           (dotimes [_ (:n args 1)]
             (insert-data! args rows table-structure)))))))

(defn dispatch-type [constraints args]
  (let [data-type (:type constraints)
        f (cond (= data-type :string)   randomized-string
                (= data-type :integer)  randomized-integer
                (= data-type :decimal)  randomized-decimal
                (= data-type :datetime) randomized-datetime
                (= data-type :binary)   randomized-blob)]
    (f (merge constraints args))))

(defn select-value [column options f]
  (partial
   (fn [column options table-args table-structure]
     (let [result (f column options table-structure table-args)]
       (bequeath-value! options result)
       {:seeds {column result} :fks [options result]}))
   column options))

(defn randomized
  ([column & {:as options}]
     (select-value
      column options
      (fn [column options table-structure _]
        (dispatch-type (get table-structure column) options)))))

(defn inherit
  ([column & {:as options}]
     (select-value
      column options
      (fn [column _ table-structure _]
        (get (:autogen table-structure) column)))))

(defn with-fn
  ([column f & {:as options}]
     (select-value column options (fn [_ _ _ _] (f)))))  

(defn value-of
  ([column value & {:as options}]
     (select-value column options (constantly value))))

(seed-table
 {:database {:db "simulation" :user "root" :password "" :vendor :mysql} :table :people}
 (randomized :name))

