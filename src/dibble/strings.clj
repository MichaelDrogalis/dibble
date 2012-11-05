(ns dibble.strings
  (:require [dibble.numbers :refer :all]))

(defn- random-string [length]
  (let [low-ascii-char 97
        high-ascii-char 122
        infinite-char-seq (repeatedly #(+ (rand-int (- high-ascii-char low-ascii-char)) low-ascii-char))]
    (apply str (map char (take length infinite-char-seq)))))

(defn randomized-string [column {:keys [length]}]
  (if-not (nil? length)
    (random-string length)
    (random-string (random-integer 0 (:max-chars column)))))

