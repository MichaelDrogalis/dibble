(ns dibble.core
  (:require [dibble.vendor :refer [describe-table clean-table!]]
            [dibble.data :as data]
            [dibble.mysql :as mysql]
            [dibble.random :as random]))

(defmulti apply-policy! (fn [args table] (:policy args)))

(defmethod apply-policy! :clean-slate [args table]
  (clean-table! args table))

(defmethod apply-policy! nil [args table])

(defn apply-policies! [args]
  (let [tables (vector (:table args))]
    (doseq [t tables]
      (apply-policy! args t))))

(defn seed-fn [column options f]
  (partial
   (fn [column options table-args table-structure]
     (let [result (f column options table-args table-structure)]
       {:seeds {column result}}))
   column options))

(defn value-of [column value & opts]
  (seed-fn column opts (constantly value)))

(defn randomized [column f & opts]
  (seed-fn column opts
           (fn [column options table-args table-structure]
             (let [constraints (get table-structure column)]
               (data/generate-data constraints options)))))

(def fn-binders
  {:value-of value-of
   :randomized randomized})

(defn seed-table [args & generation-calls]
  (let [table-structure (describe-table args)]
    (apply-policies! args)
    (doseq [_ (range (:n args 1))]
      (doseq [call generation-calls]
        ....
        (insert-row! args ....)))))

(seed-table
 {:database {:vendor :mysql :host "127.0.0.1" :port 3306
             :db "derp" :user "root" :password "password"}
  :table :things
  :policy :clean-slate}
 [:randomized :a]
 [:randomized :b])

