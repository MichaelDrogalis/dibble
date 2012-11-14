(ns dibble.test.numbers-test
  (:require [midje.sweet :refer :all]
            [clj-time.core :as time]
            [clj-time.coerce :refer :all]
            [dibble.time :refer :all]))

(fact
 (let [dt (randomized-datetime
           {}
           {:min (time/date-time 2012 1 4)
            :max (time/date-time 2012 1 6)})]
   (and (time/before? (from-date dt) (time/date-time 2012 1 7))
        (time/after?  (from-date dt) (time/date-time 2012 1 3)))
   => true))

(fact
 (from-date (randomized-datetime
             {}
             {:min (time/date-time 2012 1 4)
              :max (time/date-time 2012 1 4)}))
 => (time/date-time 2012 1 4))

