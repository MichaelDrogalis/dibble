(ns dibble.test.strings-test
  (:require [midje.sweet :refer :all]
            [dibble.strings :refer :all]))

(def column {:type :string :max-chars 32})

(fact (count (randomized-string column {:length 0})) => 0)
(fact (count (randomized-string column {:length 1})) => 1)
(fact (count (randomized-string column {:length 5})) => 5)

(fact (>= (count (randomized-string column {:min 0})) 0) => true)
(fact (>= (count (randomized-string column {:min 1})) 1) => true)
(fact (>= (count (randomized-string column {:min 5})) 5) => true)
(fact (<= (count (randomized-string column {:min 9})) 3) => false)

(fact (<= (count (randomized-string column {:max 0})) 0) => true)
(fact (<= (count (randomized-string column {:max 5})) 5) => true)
(fact (<= (count (randomized-string column {:max 9})) 9) => true)


