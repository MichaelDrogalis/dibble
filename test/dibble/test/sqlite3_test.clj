(ns dibble.test.sqlite3-test
  (:require [midje.sweet :refer :all]
            [dibble.sqlite3 :refer :all]))

(facts
 "The Sqlite3 char type is supported."
 (fact (sqlite3-to-clj-type [:name "char(1)"]) => {:name {:type :string :max-chars 1}})
 (fact (sqlite3-to-clj-type [:name "char(5)"]) => {:name {:type :string :max-chars 5}})
 (fact (sqlite3-to-clj-type [:name "char(8)"]) => {:name {:type :string :max-chars 8}}))

(facts
 "The Sqlite3 varchar type is supported."
 (fact (sqlite3-to-clj-type [:name "varchar"]) => nil)
 (fact (sqlite3-to-clj-type [:name "varchar(1)"]) => {:name {:type :string :max-chars 1}})
 (fact (sqlite3-to-clj-type [:name "varchar(10)"]) => {:name {:type :string :max-chars 10}})
 (fact (sqlite3-to-clj-type [:name "varchar(32)"]) => {:name {:type :string :max-chars 32}}))

(facts
 "The Sqlite3 tinyint, smallint, int, integer, mediumint, and bigint are supported."
 (fact (:type (:age (sqlite3-to-clj-type [:age "tinyint"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "smallint"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "integer"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "int"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "mediumint"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "bigint"]))) => :integer))

