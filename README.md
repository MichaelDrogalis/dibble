# dibble

Dibble is a Clojure library that intelligently, randomly seeds databases by inferencing the underlying table structure.

```clojure
(defseed people
  {:database {:vendor :mysql :db "db-name" :user "user" :password "pass"} :table :people :n 200}
  (randomized :name)
  (randomized :number))

(seed-table people)
```

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
(def db {:vendor :mysql :db "db-name" :user "user" :password "pass"})

;;; Makes 50 different seeds, deleting all rows in the table before beginning (using :clean-slate).
(defseed people
  {:database db :table :people :policy :clean-slate :n 50}
  (randomized :name)    ;;; random 32-char string
  (randomized :number)) ;;; random 32-bit integer

(seed-table people)
```

Values don't have to be totally random. Dibble offers some constraints:
```clojure
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

### MySQL Support
MySQL type   | Supported? | Maps to Dibble type
------------ | ---------- | -------------------
`varchar`    | Yes        | `:string`
`int`        | Yes        | `:integer`
`char`       | No         | `:string`
`tinytext`   | No         | `:string`
`text`       | No         | `:string`
`mediumtext` | No         | `:string`
`longtext`   | No         | `:string`
`tinyint`    | No         | `:integer`
`smallint`   | No         | `:integer`
`mediumint`  | No         | `:integer`
`bigint`     | No         | `:integer`
`float`      | No         | `:decimal`
`double`     | No         | `:decimal`
`decimal`    | No         | `:decimal`
`date`       | No         | ?
`datetime`   | No         | ?
`timestamp`  | No         | ?
`time`       | No         | ?
`blob`       | No         | ?
`mediumblob` | No         | ?
`longblob`   | No         | ?
`enum`       | No         | -
`set`        | No         | -

## Dibble Type Options

### `:string` Options

Option       | Usage                    | Description                                 | Mutually exclusive with
-------------|--------------------------|---------------------------------------------|------------------------
`:min-chars` | `{:min-chars 5}`         | Generated string length has a minimum of 5  | `:length`
`:max-chars` | `{:max-chars 10`         | Generated string length has a maximum of 10 | `:length`
`:length`    | `{:length 8}`            | Generated string is exactly 8 characters    | `:min-chars`, `:max-chars`
`:first-name`| `{:subtype :first-name}` | Generated string is an English first name   | `:length`, `:min-chars`, `:max-chars`
`:last-name` | `{:subtype :last-name}`  | Generated string is an English last name    | `:length`, `:min-chars`, `:max-chars`
`:full-name` | `{:subtype :full-name}`  | Generated string is an English full name    | `:length`, `:min-chars`, `:max-chars`, `:first-name`, `:last-name`

### `:integer` Options
Option       | Usage            | Description                                 | Mutually exclusive with
-------------|------------------|---------------------------------------------|------------------------
`:min`       | `{:min 5}`       | Generated integer is 5 or greater           |
`:max`       | `{:max 60}`      | Generated integer is 60 is less             |

## Dibble Policies

Policy         | Usage                    | Description
-------------- | ------------------------ | -----------
`:append`      | `{:policy :append}`      | Append seeds to what is currently in the table
`:clean-slate` | `{:policy :clean-slate}` | Delete all rows in the table before seeding

## Seeding Functions

### `randomized`

Creates a random seed of the appropriate type. Takes option to constraint generated values.

```clojure
(randomized :column & args?)                    ;;; skeleton
(randomized :name)                              ;;; random value for 'name' column
(randomized :name {:min-chars 4 :max-chars 10}) ;;; generate string between 4 and 10 chars
(randomized :name {:length 5})                  ;;; generate string of length 5
```

### `inherit`

Receive a value as a result of another seeding operation. Useful for when a column is a foreign key.

```clojure
(inherit :column) ;;; skeleton
(inherit :id)     ;;; :id will be generated as the result of another seed in another table
```

## Contribute

Contributions are very welcome. The above tables should show clearly what needs to be implemented. Fork and pull request.

## License

Copyright © 2012 Michael Drogalis

Distributed under the Eclipse Public License, the same as Clojure.

