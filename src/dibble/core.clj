(ns dibble.core
  (:require [korma.core :refer [insert delete values]]
            [korma.db :refer [default-connection _default]]
            [dire.core :refer [with-precondition! with-handler!]]
            [dibble.vendor :refer [connect describe-table]]
            [dibble.mysql :as mysql]
            [dibble.postgres :as postgres]
            [dibble.sqlite3 :as sqlite3]
            [dibble.random :as random]))

(defmacro defseed [seed-name args & rules]
  `(def ~seed-name [~args ~@rules]))

(defmacro with-connection [args & exprs]
  `(let [args# ~args
         previous-connection# @_default]
     (connect args#)
     ~@exprs
     (default-connection previous-connection#)))

(defn clean-table! [table]
  (delete table))

(defn apply-policies! [args]
  (let [tables (conj (:dependents args) (:table args))]
    (cond (= (:policy args) :clean-slate) (doall (map clean-table! tables)))))

(defn apply-external-policies! [args]
  (if-not (empty? (:external-dependents args))
    (doall (map (fn [dependent]
                  (with-connection dependent
                    (clean-table! (:table dependent))))
                (:external-dependents args)))))

(declare seed-table)

(defn select-value [column options f]
  (partial
   (fn [column options table-args table-structure]
     (let [result (f column options table-args table-structure)]
       {:seeds {column result} :fks [options result]}))
   column options))

(defmulti enqueue-data-generation :type)

(defmethod enqueue-data-generation :string [constraints] random/randomized-string)
(defmethod enqueue-data-generation :integer [constraints] random/randomized-integer)
(defmethod enqueue-data-generation :decimal [constraints] random/randomized-decimal)
(defmethod enqueue-data-generation :datetime [constraints] random/randomized-datetime)
(defmethod enqueue-data-generation :binary [constraints] random/randomized-blob)

(defn dispatch-type [constraints args]
  ((enqueue-data-generation constraints) (merge constraints args)))

(defn randomized
  ([column & {:as options}]
     (select-value
      column options
      (fn [column options _ table-structure]
        (dispatch-type (get table-structure column) options)))))

(defn inherit
  ([column & {:as options}]
     (select-value
      column options
      (fn [column _ table-args _]
        (get (:autogen table-args) column)))))

(defn with-fn
  ([column f & {:as options}]
     (select-value column options (fn [_ _ _ _] (f)))))  

(defn value-of
  ([column value & {:as options}]
     (select-value column options (constantly value))))

(defn bequeath-value! [{:keys [fk] :as args} data]
  (when fk
    (dorun
     (map
      (fn [[foreign-table foreign-column]]
        (let [gen-args [(assoc (first foreign-table) :autogen {foreign-column data})]]
          (apply seed-table (concat gen-args (rest foreign-table)))))
      fk))))

(defn bind-fn [call-seq args table-structure]
  (let [fn-binders {:randomized randomized
                    :inherit inherit
                    :with-fn with-fn
                    :value-of value-of}]
    (map (fn [call]
           ((apply ((first call) fn-binders) (rest call)) args table-structure))
         call-seq)))

(defn insert-data! [args call-seq table-structure]
  (let [fn-call-results (bind-fn call-seq args table-structure)
        seeds (apply merge (map :seeds fn-call-results))
        fks (map :fks fn-call-results)]
    (insert (:table args) (values seeds))
    (dorun (map #(apply bequeath-value! %) fks))))

(defn seed-table
  ([bundled-args] (apply seed-table bundled-args))
  ([args & generation-calls]
     (with-connection args
       (let [table-structure (describe-table args)]
         (apply-policies! args)
         (apply-external-policies! args)
         (dotimes [_ (:n args 1)]
           (insert-data! args generation-calls table-structure))))))

(with-precondition! #'seed-table
  :specifies-table
  (fn handler
    ([[args & more]] (handler args more))
    ([args & _] (contains? args :table))))

(with-handler! #'seed-table
  {:precondition :specifies-table}
  (fn [& _] (throw (Throwable. "Argument map to defseed has no :table key"))))

(with-precondition! #'seed-table
  :specifies-vendor
  (fn handler
    ([[args & more]] (handler args more))
    ([args & _] (contains? (:database args) :vendor))))

(with-handler! #'seed-table
  {:precondition :specifies-vendor}
  (fn [& _] (throw (Throwable. "Argument submap :database to defseed has no :vendor key"))))

(with-precondition! #'seed-table
  :specifies-db
  (fn handler
    ([[args & more]] (handler args more))
    ([args & _] (contains? (:database args) :db))))

(with-handler! #'seed-table
  {:precondition :specifies-db}
  (fn [& _] (throw (Throwable. "Argument submap :database to defseed has no :db key"))))

