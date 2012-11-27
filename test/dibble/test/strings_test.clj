(ns dibble.test.strings-test
  (:require [midje.sweet :refer :all]
            [dibble.random :refer :all]))

(def column {:type :string :min 0 :max 32})

(fact (count (randomized-string (merge column {:length 0}))) => 0)
(fact (count (randomized-string (merge column {:length 1}))) => 1)
(fact (count (randomized-string (merge column {:length 5}))) => 5)

(fact (>= (count (randomized-string (merge column {:min 0}))) 0) => true)
(fact (>= (count (randomized-string (merge column {:min 1}))) 1) => true)
(fact (>= (count (randomized-string (merge column {:min 5}))) 5) => true)
(fact (<= (count (randomized-string (merge column {:min 9}))) 3) => false)

(fact (<= (count (randomized-string (merge column {:max 0}))) 0) => true)
(fact (<= (count (randomized-string (merge column {:max 5}))) 5) => true)
(fact (<= (count (randomized-string (merge column {:max 9}))) 9) => true)

(fact
 (let [chars (randomized-string (merge column {:min 5 :max 7}))]
   (and (<= (count chars) 7)
        (>= (count chars) 5))
   => true))

(defn space? [char]
  (= char \space))

