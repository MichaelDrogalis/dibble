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

(fact
 (let [chars (randomized-string column {:min 5 :max 7})]
   (and (<= (count chars) 7)
        (>= (count chars) 5))
   => true))

(defn space? [char]
  (= char \space))

(fact (not-any? space?  (randomized-string column {:subtype :first-name})) => true)
(fact (not-any? space? (randomized-string column {:subtype :last-name})) => true)
(fact (count (filter space? (randomized-string column {:subtype :full-name}))) => 1)
