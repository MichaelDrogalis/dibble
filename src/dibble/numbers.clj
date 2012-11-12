(ns dibble.numbers
  (:require [clojure.math.numeric-tower :refer :all])
  (import java.util.Random))

(defn random-integer [min max]
  (+ (long (rand (- (inc max) min))) min))
  
(defn randomized-integer [column-description options]
  (let [{:keys [min max]} (merge column-description options)]
    (assert (<= min max) "Minimum bound cannot be greater than maximum bound.")
    (random-integer min max)))

(defn random-double [min max]
  (+ min (* (- max min) (.nextDouble (new Random)))))

(defn randomized-decimal [column-description options]
  (let [{:keys [min max]} (merge column-description options)]
    (assert (<= min max) "Minimum bound cannot be greater than maximum bound.")
    (random-double min max)))

