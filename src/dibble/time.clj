(ns dibble.time
  (:require [dibble.numbers :refer :all]
            [clj-time.coerce :refer :all])
  (import java.sql.Timestamp))

(defn randomized-datetime [column-map options]
  (let [{:keys [min max]} (merge column-map options)
        min-millis (to-long min)
        max-millis (to-long max)]
    (Timestamp. (random-integer min-millis max-millis))))

