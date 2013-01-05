(ns dibble.test.core-test
  (:require [midje.sweet :refer :all]
            [korma.db :refer [default-connection]]
            [clj-time.core :as time]
            [dibble.mysql :as mysql]
            [dibble.postgres :as postgres]
            [dibble.sqlite3 :as sqlite3]
            [dibble.core :refer :all]))

(facts
 "Type dispatch works by matching a standard Dibble type."
 (fact (integer? (dispatch-type {:type :integer :min 0 :max 1} {})) => true)
 (fact (string? (dispatch-type {:type :string :min 0 :max 5} {})) => true)
 (fact (float? (dispatch-type {:type :decimal :min 0 :max 1} {})) => true)
 (fact (every? integer? (dispatch-type {:type :binary :min 0 :max 2} {})) => true)
 (fact (type (dispatch-type {:type :datetime
                             :min (time/date-time 2012)
                             :max (time/date-time 2013)} {}))
       => java.sql.Timestamp))

(facts
 "The policy should be applied to the specified tables."
 (with-redefs [clean-table! identity]
   (fact (apply-policies! {:table :customers :policy :clean-slate}) => [:customers])
   (fact (apply-policies! {:policy :clean-slate}) => [nil])
   (fact (apply-policies! {:table :customers :policy :clean-slate :dependents []}) => [:customers])
   (fact (into #{}
               (apply-policies! {:table :customers :policy :clean-slate :dependents [:pets]}))
         => (into #{} [:customers :pets]))))

(fact (-> (bind-fn [[:value-of :name "Mike"]] {} {:name :string}) first :seeds :name) => "Mike")
(fact (-> (bind-fn [[:value-of :age 21]] {} {:age :integer}) first :seeds :age) => 21)

(fact (-> (bind-fn [[:with-fn :message (constantly "Hello")]] {} {:message :string})
          first :seeds :message) => "Hello")

(fact (-> (bind-fn [[:randomized :age :min 0 :max 10]] {} {:age {:type :integer}}) first :seeds :age) => integer?)
(fact (-> (bind-fn [[:inherit :id]] {:autogen {:id 10}} {:id :integer}) first :seeds :id) => 10)

