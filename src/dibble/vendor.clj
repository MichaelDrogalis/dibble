(ns dibble.vendor)

(defn vendor-name [args] (:vendor (:database args)))

(defmulti describe-table vendor-name)

(defmulti clean-table!
  (fn [args table] (vendor-name args)))

(defmulti insert-row!
  (fn [args table row] (vendor-name args)))

