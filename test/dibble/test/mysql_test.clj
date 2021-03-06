(ns dibble.test.mysql-test
  (:require [midje.sweet :refer :all]
            [dibble.mysql :refer :all]))

(facts
 "The MySQL char type is supported."
 (fact (mysql-to-clj-type [:name "char(1)"]) => {:name {:type :string :min 0 :max 1}})
 (fact (mysql-to-clj-type [:name "char(5)"]) => {:name {:type :string :min 0 :max 5}})
 (fact (mysql-to-clj-type [:name "char(8)"]) => {:name {:type :string :min 0 :max 8}})) 

(facts
 "The MySQL varchar type is supported."
 (fact (mysql-to-clj-type [:name "varchar"]) => nil)
 (fact (mysql-to-clj-type [:name "varchar(1)"]) => {:name {:type :string :min 0 :max 1}})
 (fact (mysql-to-clj-type [:name "varchar(10)"]) => {:name {:type :string :min 0 :max 10}})
 (fact (mysql-to-clj-type [:name "varchar(32)"]) => {:name {:type :string :min 0 :max 32}}))

(facts
 "The MySQL tinytext, text, mediumtext, and longtext types are supported."
 (fact (mysql-to-clj-type [:name "tinytext"]) => {:name {:type :string :min 0 :max 255}})
 (fact (mysql-to-clj-type [:name "text"]) => {:name {:type :string :min 0 :max 65535}})
 (fact (mysql-to-clj-type [:name "mediumtext"]) => {:name {:type :string :min 0 :max 16777215}})
 (fact (mysql-to-clj-type [:name "longtext"]) => {:name {:type :string :min 0 :max 4294967295}}))

(facts
 "The MySQL tinyint type is supported."
 (fact (:type (:age (mysql-to-clj-type [:age "tinyint"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "tinyint(5)"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "tinyint(11)"]))) => :integer))

(facts
 "The MySQL smallint type is supported."
 (fact (:type (:age (mysql-to-clj-type [:age "smallint"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "smallint(5)"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "smallint(11)"]))) => :integer)) 

(facts
 "The MySQL integer type is supported."
 (fact (:type (:age (mysql-to-clj-type [:age "int"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "int(5)"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "int(11)"]))) => :integer))

(facts
 "The MySQL mediumint type is supported."
 (fact (:type (:age (mysql-to-clj-type [:age "mediumint"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "mediumint(5)"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "mediumint(11)"]))) => :integer))

(facts
 "The MySQL bigint type is supported."
 (fact (:type (:age (mysql-to-clj-type [:age "bigint"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "bigint(5)"]))) => :integer)
 (fact (:type (:age (mysql-to-clj-type [:age "bigint(11)"]))) => :integer))

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

(facts
 "The MySQL datetime, date, timestamp, and time types are supported."
 (fact (:type (:modified (mysql-to-clj-type [:modified "timestamp"]))) => :datetime)
 (fact (:type (:modified (mysql-to-clj-type [:modified "datetime"]))) => :datetime)
 (fact (:type (:modified (mysql-to-clj-type [:modified "date"]))) => :datetime)
 (fact (:type (:modified (mysql-to-clj-type [:modified "time"]))) => :datetime))

(facts
 "The MySQL tinyblob, blob, mediumblob, and longblob datatypes are supported."
 (fact (:type (:image (mysql-to-clj-type [:image "tinyblob"]))) => :binary)
 (fact (:type (:image (mysql-to-clj-type [:image "blob"]))) => :binary)
 (fact (:type (:image (mysql-to-clj-type [:image "mediumblob"]))) => :binary)
 (fact (:type (:image (mysql-to-clj-type [:image "longblob"]))) => :binary))

