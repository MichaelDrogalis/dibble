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

