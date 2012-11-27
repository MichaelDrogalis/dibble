(ns dibble.core
  (:require [korma.core :refer [insert delete values]]
            [korma.db :refer [default-connection _default]]
            [dibble.mysql :as mysql]
            [dibble.postgres :as postgres]
            [dibble.sqlite3 :as sqlite3]
            [dibble.random :as random]))

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
         previous-connection# @_default]
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
                  (with-connection (vendor dependent) dependent
                    (clean-table! (:table dependent))))
                (:external-dependents args)))))

(defn bequeath-value! [{:keys [fk] :as args} data]
  (when fk
    (dorun
     (map
      (fn [[foreign-table foreign-column]]
        (let [gen-args [(assoc (first foreign-table) :autogen {foreign-column data})]]
          (apply seed-table (concat gen-args (rest foreign-table)))))
      fk))))

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
        f (cond (= data-type :string)   random/randomized-string
                (= data-type :integer)  random/randomized-integer
                (= data-type :decimal)  random/randomized-decimal
                (= data-type :datetime) random/randomized-datetime
                (= data-type :binary)   random/randomized-blob)]
    (f (merge constraints args))))

(defn select-value [column options f]
  (partial
   (fn [column options table-args table-structure]
     (let [result (f column options table-args table-structure)]
       {:seeds {column result} :fks [options result]}))
   column options))

(defn randomized
  ([column & {:as options}]
     (select-value
      column options
      (fn [column options _ table-structure]
        (dispatch-type (get table-structure column) options)))))

(defn inherit
  ([column & {:as options}]
     (select-value
      column options
      (fn [column _ table-args _]
        (get (:autogen table-args) column)))))

(defn with-fn
  ([column f & {:as options}]
     (select-value column options (fn [_ _ _ _] (f)))))  

(defn value-of
  ([column value & {:as options}]
     (select-value column options (constantly value))))



;;; Probably stored in a credentials Clojure file.
(def rjmadmin {:db "rjmadmin" :user "root" :password "" :vendor :mysql})
(def rjmdash {:db "rjmdashboards" :user "root" :password "" :vendor :mysql})

;;; A few helper functions. Seeing how people use this will help me to figure
;;; out what to put in the API. For example, a function called "choose" will
;;; probably go into the next release, which does the following:
(defn random-browser []
  (rand-nth ["firefox" "chrome" "ie"]))

(defn random-os []
  (rand-nth ["windows" "linux" "xos x"]))

(defn get-hash-str [data-bytes]
  (apply str (map #(.substring (Integer/toString (+ (bit-and % 0xff) 0x100) 16) 1) data-bytes)))

(defn random-hash []
  (get-hash-str (.digest (java.security.MessageDigest/getInstance "sha1") (.getBytes (random/random-string 32)))))

(defseed users
  {:database rjmadmin :table :rjm_users}
  (inherit :uid)
  (randomized :salt)
  (randomized :name :subtype :full-name)
  (randomized :email)
  (with-fn :saltedpw random-hash)
  (with-fn :browser random-browser)
  (with-fn :os random-os))

(seed-table
 {:database rjmdash :table :charts :policy :clean-slate :n 5 :external-dependents [{:database rjmadmin :table :rjm_users}]}
 (randomized :chid)
 (randomized :cid)
 (randomized :uid :fk {users :uid})
 (randomized :categories)
 (randomized :catvalues)
 (randomized :restrictions)
 (randomized :restseq)
 (randomized :sizelimit)
 (randomized :dfid))

