(ns dibble.numbers)

(defn- random-integer [low high]
  (first (shuffle (range low (inc high)))))

(defn randomized-integer [column]
  (Integer/parseInt (apply str (map (fn [_] (first (shuffle (range 0 2)))) (range 0 (dec (* (:bytes column) 8))))) 2))

