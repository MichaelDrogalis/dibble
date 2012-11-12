(ns dibble.test.numbers-test
  (:require [midje.sweet :refer :all]
            [dibble.numbers :refer :all]))

(fact (let [n (random-integer 0 1)] (and (<= n 1) (>= n 0))) => true)
(fact (let [n (random-integer 0 9)] (and (<= n 9) (>= n 0))) => true)

(fact(let [n (random-integer -10 10)]
        (and (<= n 10) (>= n -10)) => true))

(fact (let [n (random-integer -10 -5)]
        (and (<= n -5) (>= n -10)) => true))

(def integer-column {:type :integer :bytes 4})

(fact (>= (randomized-integer integer-column {:min 0}) 0) => true)
(fact (>= (randomized-integer integer-column {:min 5}) 5) => true)

(fact (<= (randomized-integer integer-column {:max 0}) 0) => true)
(fact (<= (randomized-integer integer-column {:max 5}) 5) => true)

(fact
 (let [n (randomized-integer integer-column {:min 5 :max 6})]
   (and (<= n 6) (>= n 5))) => true)

(fact (randomized-integer integer-column {:min 5 :max 5}) => 5)

(def double-column {:type :double})

(fact (float? (randomized-decimal double-column {})) => true)

(fact (>= (randomized-decimal double-column {:min 3.14}) 3.14) => true)
(fact (<= (randomized-decimal double-column {:max 3.14}) 3.14) => true)

(fact
 (let [n (randomized-decimal double-column {:min 1.23 :max 2.01})]
   (and (>= n 1.23) (<= n 2.01))) => true)