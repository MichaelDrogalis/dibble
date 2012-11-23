(ns dibble.test.core-test
  (:require [midje.sweet :refer :all]
            [clj-time.core :as time]
            [dibble.mysql :as mysql]
            [dibble.postgres :as postgres]
            [dibble.sqlite3 :as sqlite3]
            [dibble.core :refer :all]))

(facts
 "Type dispatch works by matching a standard Dibble type."
 (fact (integer? (dispatch-type {:type :integer :min 0 :max 1} {})) => true)
 (fact (string? (dispatch-type {:type :string :min 0 :max 5} {})) => true)
 (fact (float? (dispatch-type {:type :decimal :min 0 :max 1} {})) => true)
 (fact (every? integer? (dispatch-type {:type :binary :min 0 :max 2} {})) => true)
 (fact (type (dispatch-type {:type :datetime
                             :min (time/date-time 2012)
                             :max (time/date-time 2013)} {}))
       => java.sql.Timestamp))

(facts
 "Database spec parsing returns a connection specific to each vendor."
 (with-redefs [mysql/mysql-db (constantly :mysql-connection)
               postgres/postgres-db (constantly :postgres-connection)
               sqlite3/sqlite3-db (constantly :sqlite3-connection)]
   (fact (table-description {:database {:vendor :mysql}}) => :mysql-connection)
   (fact (table-description {:database {:vendor :postgres}}) => :postgres-connection)
   (fact (table-description {:database {:vendor :sqlite3}}) => :sqlite3-connection)))

(facts
 "The policy should be applied to the specified tables."
 (with-redefs [clean-table identity]
   (fact (apply-policies! {:table :customers :policy :clean-slate}) => [:customers])
   (fact (apply-policies! {:policy :clean-slate}) => [nil])
   (fact (apply-policies! {:table :customers :policy :clean-slate :dependents []}) => [:customers])
   (fact (into #{}
               (apply-policies! {:table :customers :policy :clean-slate :dependents [:pets]}))
         => (into #{} [:customers :pets]))))

 
