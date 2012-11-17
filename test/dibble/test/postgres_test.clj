(ns dibble.test.postgres-test
  (:require [midje.sweet :refer :all]
            [dibble.postgres :refer :all]))

(facts
 "The Postgres smallint, integer, and bigint types are supported."
 (fact (:type (:age (postgres-to-clj-type [:age "smallint"]))) => :integer)
 (fact (:type (:age (postgres-to-clj-type [:age "integer"]))) => :integer)
 (fact (:type (:age (postgres-to-clj-type [:age "bigint"]))) => :integer))

(facts
 "The Postgres real and double precision types are supported."
 (fact (:type (:number (postgres-to-clj-type [:number "real"]))) => :decimal)
 (fact (:type (:number (postgres-to-clj-type [:number "double precision"]))) => :decimal))

(facts
 "The Postgres decimal and numeric types are supported."
 (fact (:type (:number (postgres-to-clj-type [:number "decimal"]))) => :decimal)
 (fact (:type (:number (postgres-to-clj-type [:number "numeric"]))) => :decimal))

(facts
 "The Postgres char, varchar, and text types are supported."
 (fact (postgres-to-clj-type [:name "char(5)"]) => {:name {:type :string :max-chars 5}})
 (fact (postgres-to-clj-type [:name "character(5)"]) => {:name {:type :string :max-chars 5}})
 (fact (postgres-to-clj-type [:name "varchar"]) => nil)
 (fact (postgres-to-clj-type [:name "varchar(1)"]) => {:name {:type :string :max-chars 1}})
 (fact (postgres-to-clj-type [:name "varchar(10)"]) => {:name {:type :string :max-chars 10}})
 (fact (postgres-to-clj-type [:name "character varying(10)"]) => {:name {:type :string :max-chars 10}})
 (fact (:type (:name (postgres-to-clj-type [:name "text"]))) => :string))

(facts
 "The Postgres bytea type is supported."
 (fact (:type (:image (postgres-to-clj-type [:image "bytea"]))) => :binary))

