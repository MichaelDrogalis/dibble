(ns dibble.test.core-test
  (:require [midje.sweet :refer :all]
            [clj-time.core :as time]
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