# dibble

Dibble is a Clojure library that intelligently, randomly seeds databases by inferencing the underlying table structure.

## Installation

Available on Clojars:

    [dibble "0.1.0-SNAPSHOT"]

## Usage

A few complete examples follow. Starting with the simplest case, suppose we have a MySQL table named `people`:
```sql
mysql> desc people;
+--------+---------------+------+-----+---------+-------+
| Field  | Type          | Null | Key | Default | Extra |
+--------+---------------+------+-----+---------+-------+
| name   | varchar(32)   | YES  |     | NULL    |       |
| number | int(11)       | YES  |     | NULL    |       |
+--------+---------------+------+-----+---------+-------+
```

Let's seed that table:
```clojure
(ns my.seeder
  (:require [dibble.core :refer :all]))

;;; A map that specifies the database type via :vendor and Korma connection information.
(def db {:vendor :mysql :db "simulation" :user "root" :password ""})

;;; Makes 50 different seeds, deleting all rows in the table before beginning (using :clean-slate).
(defseed people
  {:database db :table :people :policy :clean-slate :n 50}
  (randomized :name)    ;;; random 32-char string
  (randomized :number)) ;;; random 32-bit integer

(seed-table people)
```

Values don't have to be totally random. Dibble offers some constraints:
```clojure
(ns my.seeder
  (:require [dibble.core :refer :all]))

(def db {:vendor :mysql :db "simulation" :user "root" :password ""})

(defseed people
  {:database db :table :people :policy :clean-slate :n 50}
  (randomized :name {:length 16})        ;;; random 16-char string
  (randomized :number {:min 5 :max 10})) ;;; random integer between 5 and 10 inclusive

(seed-table people)
```

Dibble handles tables that have foreign keys. Let's introduce another table to up the complexity a smidge:
```sql
mysql> desc pets;
+-------+-------------+------+-----+---------+-------+
| Field | Type        | Null | Key | Default | Extra |
+-------+-------------+------+-----+---------+-------+
| pid   | int(11)     | YES  |     | NULL    |       |
| name  | varchar(32) | YES  |     | NULL    |       |
+-------+-------------+------+-----+---------+-------+
```

Suppose the `number` column in the `people` table refers to the `pid` column of `pets`, effectively making `pid` a foreign key. Each time we generate a seed for a row in the `people` table, we want to generate a row in the `pets` table so the data makes semantic sense. Here's the code to do that:

```clojure
(ns dibble.seeder
  (:require [dibble.core :refer :all]))

(def db {:vendor :mysql :db "simulation" :user "root" :password ""})

(defseed pets
  {:database db :table :pets} ;;; don't specify a policy since it depends on other tables
  (inherit :pid)              ;;; :pid is given to us from another row in another table
  (randomized :name))

;;; Policy :transitive specifies deleting all rows in all tables of the :dependents sequence
;;; before seeding.
(defseed people
  {:database db :table :people :policy :transitive :dependents [:pets] :n 50}
  (randomized :name)
  (randomized :number {:fk [pets :pid]})) ;;; :fk specifies a vector of a table and column to place :number into

(seed-table people)
```

## Supported Databases

Database | Supported?
-------- | ----------
MySQL    | Yes
Postgres | Not yet
Oracle   | Not yet
MSSQL    | Not yet
SQLite   | Not yet

## License

Copyright © 2012 Michael Drogalis

Distributed under the Eclipse Public License, the same as Clojure.

