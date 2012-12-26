(ns dibble.vendor)

(defn vendor-name [args] (:vendor (:database args)))

(defmulti connect vendor-name)
(defmulti describe-table vendor-name)

