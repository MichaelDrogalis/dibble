(ns dibble.numbers
  (:require [clojure.math.numeric-tower :refer :all])
  (import java.util.Random))

(defn random-integer [min max]
  (+ (long (rand (- (inc max) min))) min))
  
(defn randomized-integer [column-description options]
  (let [args (merge column-description options)]
    (random-integer (:min args) (:max args))))

(defn random-double [min max]
  (+ min (* (- max min) (.nextDouble (new Random)))))

(defn randomized-decimal [column {:keys [min max]}]
  (cond (and (not (nil? min)) (not (nil? max))) (random-double min max)
        (and (not (nil? min)) (nil? max)) (random-double min Short/MAX_VALUE)
        (and (nil? min) (not (nil? max))) (random-double Short/MIN_VALUE max)
        :else (random-double Short/MIN_VALUE Short/MAX_VALUE)))

