(ns dibble.test.mysql-test
  (:require [midje.sweet :refer :all]
            [dibble.mysql :refer :all]))

(facts
 "The MySQL varchar type is supported."
 (fact (mysql-to-clj-type [:name "varchar"]) => nil)
 (fact (mysql-to-clj-type [:name "varchar(1)"]) => {:name {:type :string :max-chars 1}})
 (fact (mysql-to-clj-type [:name "varchar(10)"]) => {:name {:type :string :max-chars 10}})
 (fact (mysql-to-clj-type [:name "varchar(32)"]) => {:name {:type :string :max-chars 32}}))

(facts
 "The MySQL integer type is supported."
 (fact (:type (:age (mysql-to-clj-type [:age "int"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "int(5)"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "int(11)"]))) => :integer))

(facts
 "The MySQL tinyint type is supported."
 (fact (:type (:age (mysql-to-clj-type [:age "tinyint"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "tinyint(5)"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "tinyint(11)"]))) => :integer))

(facts
 "The MySQL double type is supported."
 (fact (:type (:balance (mysql-to-clj-type [:balance "double"]))) => :decimal)
 (fact (:type (:balance (mysql-to-clj-type [:balance "double(5,4)"]))) => :decimal))

(facts
 "The MySQL float type is supported."
 (fact (:type (:balance (mysql-to-clj-type [:balance "float"]))) => :decimal)
 (fact (:type (:balance (mysql-to-clj-type [:balance "float(5,2)"]))) => :decimal))

(facts
 "The MySQL decimal type is supported."
 (fact (:type (:balance (mysql-to-clj-type [:balance "decimal"]))) => :decimal)
 (fact (:type (:balance (mysql-to-clj-type [:balance "decimal(8,3)"]))) => :decimal))

