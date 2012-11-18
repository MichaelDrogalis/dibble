(ns dibble.test.sqlite3-test
  (:require [midje.sweet :refer :all]
            [dibble.sqlite3 :refer :all]))

(facts
 "The SQLite3 char type is supported."
 (fact (sqlite3-to-clj-type [:name "char(1)"]) => {:name {:type :string :min 0 :max 1}})
 (fact (sqlite3-to-clj-type [:name "char(5)"]) => {:name {:type :string :min 0 :max 5}})
 (fact (sqlite3-to-clj-type [:name "char(8)"]) => {:name {:type :string :min 0 :max 8}})) 

(facts
 "The SQLite3 varchar type is supported."
 (fact (sqlite3-to-clj-type [:name "varchar"]) => nil)
 (fact (sqlite3-to-clj-type [:name "varchar(1)"]) => {:name {:type :string :min 0 :max 1}})
 (fact (sqlite3-to-clj-type [:name "varchar(10)"]) => {:name {:type :string :min 0 :max 10}})
 (fact (sqlite3-to-clj-type [:name "varchar(32)"]) => {:name {:type :string :min 0 :max 32}}))

(facts
 "The SQLite3 tinytext, text, mediumtext, and longtext types are supported."
 (fact (sqlite3-to-clj-type [:name "tinytext"]) => {:name {:type :string :min 0 :max 255}})
 (fact (sqlite3-to-clj-type [:name "text"]) => {:name {:type :string :min 0 :max 65535}})
 (fact (sqlite3-to-clj-type [:name "mediumtext"]) => {:name {:type :string :min 0 :max 16777215}})
 (fact (sqlite3-to-clj-type [:name "longtext"]) => {:name {:type :string :min 0 :max 4294967295}}))

(facts
 "The SQLite3 tinyint type is supported."
 (fact (:type (:age (sqlite3-to-clj-type [:age "tinyint"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "tinyint(5)"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "tinyint(11)"]))) => :integer))

(facts
 "The SQLite3 smallint type is supported."
 (fact (:type (:age (sqlite3-to-clj-type [:age "smallint"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "smallint(5)"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "smallint(11)"]))) => :integer)) 

(facts
 "The SQLite3 integer type is supported."
 (fact (:type (:age (sqlite3-to-clj-type [:age "int"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "int(5)"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "int(11)"]))) => :integer))

(facts
 "The SQLite3 mediumint type is supported."
 (fact (:type (:age (sqlite3-to-clj-type [:age "mediumint"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "mediumint(5)"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "mediumint(11)"]))) => :integer))

(facts
 "The SQLite3 bigint type is supported."
 (fact (:type (:age (sqlite3-to-clj-type [:age "bigint"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "bigint(5)"]))) => :integer)
 (fact (:type (:age (sqlite3-to-clj-type [:age "bigint(11)"]))) => :integer))

(facts
 "The SQLite3 double type is supported."
 (fact (:type (:balance (sqlite3-to-clj-type [:balance "double"]))) => :decimal)
 (fact (:type (:balance (sqlite3-to-clj-type [:balance "double(5,4)"]))) => :decimal))

(facts
 "The SQLite3 float type is supported."
 (fact (:type (:balance (sqlite3-to-clj-type [:balance "float"]))) => :decimal)
 (fact (:type (:balance (sqlite3-to-clj-type [:balance "float(5,2)"]))) => :decimal))

(facts
 "The SQLite3 decimal type is supported."
 (fact (:type (:balance (sqlite3-to-clj-type [:balance "decimal"]))) => :decimal)
 (fact (:type (:balance (sqlite3-to-clj-type [:balance "decimal(8,3)"]))) => :decimal))

(facts
 "The SQLite3 datetime, date, timestamp, and time types are supported."
 (fact (:type (:modified (sqlite3-to-clj-type [:modified "timestamp"]))) => :datetime)
 (fact (:type (:modified (sqlite3-to-clj-type [:modified "datetime"]))) => :datetime)
 (fact (:type (:modified (sqlite3-to-clj-type [:modified "date"]))) => :datetime)
 (fact (:type (:modified (sqlite3-to-clj-type [:modified "time"]))) => :datetime))

(facts
 "The SQLite3 tinyblob, blob, mediumblob, and longblob datatypes are supported."
 (fact (:type (:image (sqlite3-to-clj-type [:image "tinyblob"]))) => :binary)
 (fact (:type (:image (sqlite3-to-clj-type [:image "blob"]))) => :binary)
 (fact (:type (:image (sqlite3-to-clj-type [:image "mediumblob"]))) => :binary)
 (fact (:type (:image (sqlite3-to-clj-type [:image "longblob"]))) => :binary))

(facts
 "User's can define the type map themselves due to SQLite3's dynamic type system."
 (fact (merge {:name nil} {:name :integer}) => {:name :integer})
 (fact (merge {:name :integer} {}) => {:name :integer})
 (fact (merge {:zipcode :string} {:zipcode :integer}) => {:zipcode :integer})
 (fact (merge {:name :string :age nil} {:age :decimal}) => {:name :string :age :decimal}))

