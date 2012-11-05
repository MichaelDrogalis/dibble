(ns dibble.strings)

(defn random-string [length]
  (let [low-ascii-char 97
        high-ascii-char 122
        infinite-char-seq (repeatedly #(+ (rand-int (- high-ascii-char low-ascii-char)) low-ascii-char))]
    (apply str (map char (take length infinite-char-seq)))))

