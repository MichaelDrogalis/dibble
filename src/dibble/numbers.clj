(ns dibble.numbers
  (import java.util.Random))

(defn random-integer [min max]
  (+ (.nextInt (new Random) (- (inc max) min)) min))

(defn randomized-integer [column {:keys [min max]}]
  (cond (and (not (nil? min)) (not (nil? max))) (random-integer min max)
        (and (not (nil? min)) (nil? max)) (random-integer min Integer/MAX_VALUE)
        (and (nil? min) (not (nil? max))) (random-integer Integer/MIN_VALUE max)
        :else (random-integer Integer/MIN_VALUE Integer/MAX_VALUE)))

