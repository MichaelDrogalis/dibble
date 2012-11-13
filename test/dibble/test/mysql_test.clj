(ns dibble.test.mysql-test
  (:require [midje.sweet :refer :all]
            [dibble.mysql :refer :all]))

(fact (mysql-to-clj-type [:name "varchar"]) => nil)
(fact (mysql-to-clj-type [:name "varchar(1)"]) => {:name {:type :string :max-chars 1}})
(fact (mysql-to-clj-type [:name "varchar(10)"]) => {:name {:type :string :max-chars 10}})
(fact (mysql-to-clj-type [:name "varchar(32)"]) => {:name {:type :string :max-chars 32}})

(fact (:type (:age (mysql-to-clj-type [:age "int"]))) => :integer)
(fact (:type (:age (mysql-to-clj-type [:age "int(5)"]))) => :integer)
(fact (:type (:age (mysql-to-clj-type [:age "int(11)"]))) => :integer)

(fact (:type (:balance (mysql-to-clj-type [:balance "double"]))) => :decimal)
(fact (:type (:balance (mysql-to-clj-type [:balance "double(5,4)"]))) => :decimal)

(fact (:type (:balance (mysql-to-clj-type [:balance "float"]))) => :decimal)
(fact (mysql-to-clj-type [:balance "float(5,2)"]) => {:balance {:type :decimal :min -999.0 :max 999.0}})

(fact (mysql-to-clj-type [:balance "decimal"]) => {:balance {:type :decimal :precision 10 :accuracy 0}})
(fact (mysql-to-clj-type [:balance "decimal(8,3)"]) => {:balance {:type :decimal :precision 8 :accuracy 3}})
