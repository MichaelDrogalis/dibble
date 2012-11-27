(ns dibble.random
  (:require [clj-time.coerce :refer [to-long]]
            [cheshire.core :refer [parse-string]])
  (import java.sql.Timestamp)
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

(defn randomized-datetime [{:keys [min max]}]
  (let [min-millis (to-long min)
        max-millis (to-long max)]
    (Timestamp. (random-integer min-millis max-millis))))

(defn randomized-blob [{:keys [min max]}]
  (byte-array (drop min (take (rand max) (repeatedly #(byte (rand 128)))))))

(def names (parse-string (slurp (.getFile (clojure.java.io/resource "names.json"))) true))
(def first-names (:first-names names))
(def last-names (:last-names names))

(defn random-first-name []
  (rand-nth first-names))

(defn random-last-name []
  (rand-nth last-names))

(defn random-string [length]
  (let [low-ascii-char 97
        high-ascii-char 122
        infinite-char-seq (repeatedly #(random-integer low-ascii-char high-ascii-char))]
    (apply str (map char (take length infinite-char-seq)))))

(defn randomized-string [{:keys [min max length subtype]}]
  (cond (= subtype :first-name) (random-first-name)
        (= subtype :last-name) (random-last-name)
        (= subtype :full-name) (str (random-first-name) " " (random-last-name))
        (not (nil? length)) (random-string length)
        :else (random-string (random-integer min max))))

