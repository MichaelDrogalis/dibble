(ns dibble.test.numbers-test
  (:require [midje.sweet :refer :all]
            [clj-time.core :refer [date-time before? after?]]
            [clj-time.coerce :refer [from-date]]
            [dibble.random :refer :all]))

(fact
 (let [dt (randomized-datetime
           {:min (date-time 2012 1 4)
            :max (date-time 2012 1 6)})]
   (and (before? (from-date dt) (date-time 2012 1 7))
        (after?  (from-date dt) (date-time 2012 1 3)))
   => true))

(fact
 (from-date (randomized-datetime
             {:min (date-time 2012 1 4)
              :max (date-time 2012 1 4)}))
 => (date-time 2012 1 4))

