(ns dibble.vendor)

(defn vendor-name [args] (:vendor (:database args)))

(defmulti describe-table vendor-name)

(defmulti clean-table!
  (fn [args table] (vendor-name args)))

