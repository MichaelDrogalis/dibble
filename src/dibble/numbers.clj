(ns dibble.numbers)

(defn random-integer [low high]
  (first (shuffle (range low (inc high)))))

(defn randomized-integer [column args]
  (Integer/parseInt (apply str (map (fn [_] (rand-nth [0 1])) (range 0 (dec (* (:bytes column) 8))))) 2))

