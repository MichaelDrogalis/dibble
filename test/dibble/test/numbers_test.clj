(ns dibble.test.numbers-test
  (:require [midje.sweet :refer :all]
            [dibble.numbers :refer :all]))

(fact (let [n (random-integer 0 1)] (and (<= n 1) (>= n 0))) => true)
(fact (let [n (random-integer 0 9)] (and (<= n 9) (>= n 0))) => true)

(fact(let [n (random-integer -10 10)]
        (and (<= n 10) (>= n -10)) => true))

(fact (let [n (random-integer -10 -5)]
        (and (<= n -5) (>= n -10)) => true))

(def column {:type :integer :bytes 4})

(fact (>= (randomized-integer column {:min 0}) 0) => true)
(fact (>= (randomized-integer column {:min 5}) 5) => true)

(fact (<= (randomized-integer column {:max 0}) 0) => true)
(fact (<= (randomized-integer column {:max 5}) 5) => true)

(fact
 (let [n (randomized-integer column {:min 5 :max 6})]
   (and (<= n 6) (>= n 5))) => true)

(fact (randomized-integer column {:min 5 :max 5}) => 5)

