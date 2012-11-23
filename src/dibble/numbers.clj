(ns dibble.numbers
  (:require [clojure.math.numeric-tower :refer :all])
  (import java.util.Random))

(defn generate-value-in-range [{:keys [min max]} f]
  (assert (<= min max) "Minimum bound cannot be greater than maximum bound.")
  (f min max))

(defn random-integer [min max]
  (if (and (< min 0) (< max 0))
    (* -1 (random-integer (* -1 min) (* -1 max)))
    (long (+ (* (rand) (inc (- (bigint max) (bigint min)))) min))))

(defn randomized-integer [options]
  (generate-value-in-range options random-integer))

(defn random-double [min max]
  (+ min (* (- max min) (.nextDouble (new Random)))))

(defn randomized-decimal [options]
  (generate-value-in-range options random-double))

