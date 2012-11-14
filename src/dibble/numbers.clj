(ns dibble.numbers
  (:require [clojure.math.numeric-tower :refer :all])
  (import java.util.Random))

(defn generate-value-in-range [column-map options f]
  (let [{:keys [min max]} (merge column-map options)]
    (assert (<= min max) "Minimum bound cannot be greater than maximum bound.")
    (f min max)))

(defn random-integer [min max]
  (long (+ (bigint (* (rand) (inc (- max min)))) min)))

(defn randomized-integer [column-map options]
  (generate-value-in-range column-map options random-integer))

(defn random-double [min max]
  (+ min (* (- max min) (.nextDouble (new Random)))))

(defn randomized-decimal [column-map options]
  (generate-value-in-range column-map options random-double))

