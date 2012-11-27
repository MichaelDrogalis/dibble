(ns dibble.test.binary-test
  (:require [midje.sweet :refer :all]
            [dibble.random :refer :all]))

(fact (some #{(count (randomized-blob {:min 0 :max 5}))}
            (range 0 6)) =not=> nil)

(fact (some #{(count (randomized-blob {:min 0 :max 0}))}
            (list 0)) =not=> nil)

(fact (some #{(count (randomized-blob {:min 0 :max 8}))}
            (range 0 9)) =not=> nil)

