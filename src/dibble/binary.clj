(ns dibble.binary)

(defn randomized-blob [{:keys [min max]}]
  (byte-array (drop min (take (rand max)
                              (repeatedly #(byte (rand 128)))))))

