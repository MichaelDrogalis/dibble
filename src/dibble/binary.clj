(ns dibble.binary)

(defn randomized-blob [column-map options]
  (let [{:keys [min max]} (merge column-map options)]
    (byte-array (drop min (take (rand max)
                                (repeatedly #(byte (rand 128))))))))

