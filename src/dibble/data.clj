(ns dibble.data
  (:require [clj-time.coerce :refer [to-long]])
  (:import [java.util Random]
           [java.sql Timestamp]))

(def first-names
  (read-string (slurp (clojure.java.io/resource "first-names.edn"))))

(def last-names
  (read-string (slurp (clojure.java.io/resource "last-names.edn"))))

(defn random-integer [min max]
  (if (and (< min 0) (< max 0))
    (* -1 (random-integer (* -1 min) (* -1 max)))
    (long (+ (* (rand) (inc (- (bigint max) (bigint min)))) min))))

(defn random-double [min max]
  (+ min (* (- max min) (.nextDouble (Random.)))))

(defmulti random-string :subtype)

(defmethod random-string :first-name [opts]
  (rand-nth first-names))

(defmethod random-string :last-name [opts]
  (rand-nth last-names))

(defmethod random-string :full-name [opts]
  (str (random-string {:subtype :first-name}) " "
       (random-string {:subtype :last-name})))

(defmethod random-string :default [opts]
  (let [low-char 97
        high-char 122
        int-seq (repeatedly #(random-integer low-char high-char))
        bounded-seq (if (:length opts)
                      (take (:length opts) int-seq)
                      (take (random-integer (:min opts) (:max opts)) int-seq))]
    (apply str (map char bounded-seq))))

(defmulti generate-data (fn [constraints _] (:type constraints)))

(defmethod generate-data :string [constraints args]
  (let [opts (merge constraints args)]
    (random-string opts)))

(defmethod generate-data :integer [constraints args]
  (let [opts (merge constraints args)]
    (random-integer (:min opts) (:max opts))))

(defmethod generate-data :decimal [constraints args]
  (let [opts (merge constraints args)]
    (random-double (:min opts) (:max opts))))

(defmethod generate-data :inst [constraints args]
  (let [opts (merge constraints args)]
    (Timestamp. (random-integer (to-long (:min opts) (:max opts))))))

