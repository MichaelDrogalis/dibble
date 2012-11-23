(ns dibble.time
  (:require [dibble.numbers :refer :all]
            [clj-time.coerce :refer :all])
  (import java.sql.Timestamp))

(defn randomized-datetime [{:keys [min max]}]
  (let [min-millis (to-long min)
        max-millis (to-long max)]
    (Timestamp. (random-integer min-millis max-millis))))

