(ns dibble.test.sqlite3-test
  (:require [midje.sweet :refer :all]
            [dibble.sqlite3 :refer :all]))

(facts
 "The Sqlite3 varchar type is supported."
 (fact (sqlite3-to-clj-type [:name "varchar"]) => nil)
 (fact (sqlite3-to-clj-type [:name "varchar(1)"]) => {:name {:type :string :max-chars 1}})
 (fact (sqlite3-to-clj-type [:name "varchar(10)"]) => {:name {:type :string :max-chars 10}})
 (fact (sqlite3-to-clj-type [:name "varchar(32)"]) => {:name {:type :string :max-chars 32}}))

