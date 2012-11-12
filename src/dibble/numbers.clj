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

(defn randomized-decimal [column-description options]
  (let [args (merge column-description options)]
    (random-double (:min args) (:max args))))

