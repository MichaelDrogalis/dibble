(ns dibble.time
  (:require [clj-time.coerce :refer :all])
  (import java.sql.Timestamp))

(defn randomized-time [column-map description]
  (Timestamp. (to-long (:max column-map))))

