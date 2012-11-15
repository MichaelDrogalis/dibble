(ns dibble.binary)

(defn randomized-blob [column-map options]
  (let [{:keys [min max]} (merge column-map options)]
    (byte-array (drop min (take max (repeatedly #(byte (rand 128))))))))

