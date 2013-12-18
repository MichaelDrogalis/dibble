(ns dibble.data
  (:require [clj-time.coerce :refer [to-long]])
  (:import [java.util Random]
           [java.sql Timestamp]))

(defmulti random-string :subtype)

(defmethod random-string :first-name [opts]
  "Jane")

(defmethod random-string :last-name [opts]
  "Doe")

(defmethod random-string :full-name [opts]
  "Jack Doe")

(defmethod random-string :default [opts]
  (if (:length opts)
    "Fixed len"
    "Var len"))

(defn random-integer [min max]
  (if (and (< min 0) (< max 0))
    (* -1 (random-integer (* -1 min) (* -1 max)))
    (long (+ (* (rand) (inc (- (bigint max) (bigint min)))) min))))

(defn random-double [min max]
  (+ min (* (- max min) (.nextDouble (Random.)))))

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

