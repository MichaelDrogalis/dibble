# dibble <a href="https://travis-ci.org/MichaelDrogalis/dibble"><img src="https://api.travis-ci.org/MichaelDrogalis/dibble.png" /></a>

Dibble is a Clojure library that intelligently, randomly seeds databases by inferencing the underlying table structure.
See the [docs](http://michaeldrogalis.github.com/dibble) for more more information.

```clojure
(defseed people
  {:database {:vendor :mysql :db "db-name" :user "user" :password "pass"} :table :people :n 200}
  [:randomized :name]
  [:randomized :number])

(seed-table people)
```

## Installation

Available on Clojars:

    [dibble "0.1.4"]

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
  (:require [dibble.core :refer [defseed seed-table]]))

;;; A map that specifies the database type via :vendor and Korma connection information.
(def db {:vendor :mysql :db "db-name" :user "user" :password "pass"})

;;; Makes 50 different seeds, deleting all rows in the table before beginning (using :clean-slate).
(defseed people
  {:database db :table :people :policy :clean-slate :n 50}
  [:randomized :name]    ;;; random 32-char string
  [:randomized :number]) ;;; random 32-bit integer

(seed-table people)
```

Values don't have to be totally random. Dibble offers some constraints:
```clojure
(defseed people
  {:database db :table :people :policy :clean-slate :n 50}
  [:randomized :name {:length 16}]        ;;; random 16-char string
  [:randomized :number {:min 5 :max 10}]) ;;; random integer between 5 and 10 inclusive

(seed-table people)
```

## Design

![Design](http://i48.tinypic.com/2nbampd.png)

## Contribute

Contributions are most welcome. There's a ton of work to do, and it's fairly easy to contribute.
Here's some things I'd like to have that aren't done:

- More string options (subtypes of email address, random US state, etc)
- More numeric options (prime?, symbols?, spaces? etc)
- Possibly rewrite the docs to be a little clearer?
- More database implementations (specifically Mongo)

## License

Copyright © 2012 Michael Drogalis

Distributed under the Eclipse Public License, the same as Clojure.

