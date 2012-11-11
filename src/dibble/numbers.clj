(ns dibble.numbers
  (:require [clojure.math.numeric-tower :refer :all])
  (import java.util.Random))

(defn random-integer [min max]
  (+ (long (rand (- (inc max) min))) min))
  
(defn randomized-integer [column {:keys [min max]}]
  (cond (and (not (nil? min)) (not (nil? max))) (random-integer min max)
        (and (not (nil? min)) (nil? max)) (random-integer min Integer/MAX_VALUE)
        (and (nil? min) (not (nil? max))) (random-integer Integer/MIN_VALUE max)
        :else (random-integer Integer/MIN_VALUE Integer/MAX_VALUE)))

(defn random-double [min max]
  (+ min (* (- max min) (.nextDouble (new Random)))))

(defn randomized-double [column {:keys [min max]}]
  (cond (and (not (nil? min)) (not (nil? max))) (random-double min max)
        (and (not (nil? min)) (nil? max)) (random-double min Short/MAX_VALUE)
        (and (nil? min) (not (nil? max))) (random-double Short/MIN_VALUE max)
        :else (random-double -10 10)))

