(ns dibble.test.postgres-test
  (:require [midje.sweet :refer :all]
            [dibble.postgres :refer :all]))

(facts
 "The Postgres smallint, integer, and bigint types are supported."
 (fact (:type (:age (postgres-to-clj-type [:age "smallint"]))) => :integer)
 (fact (:type (:age (postgres-to-clj-type [:age "integer"]))) => :integer)
 (fact (:type (:age (postgres-to-clj-type [:age "bigint"]))) => :integer))

(facts
 "The Postgres real type is supported."
 (fact (:type (:number (postgres-to-clj-type [:number "real"]))) => :decimal))

