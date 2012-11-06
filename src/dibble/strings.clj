(ns dibble.strings
  (:require [dibble.numbers :refer :all]))

(defn- random-string [length]
  (let [low-ascii-char 97
        high-ascii-char 122
        infinite-char-seq (repeatedly #(+ (rand-int (- high-ascii-char low-ascii-char)) low-ascii-char))]
    (apply str (map char (take length infinite-char-seq)))))

(defn randomized-string [column {:keys [length min max]}]
  (cond (not (nil? length)) (random-string length)
        (and (not (nil? min)) (not (nil? max))) (random-string (random-integer min max))
        (and (not (nil? min)) (nil? max)) (random-string (random-integer min (:max-chars column)))
        (and (nil? min) (not (nil? max))) (random-string (random-integer 0 max))
        :else (random-string (random-integer 0 (:max-chars column)))))

