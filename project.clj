(defproject dibble "0.3.0-SNAPSHOT"
  :description "A Clojure library for seeding databases"
  :url "https://github.com/MichaelDrogalis/dibble"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0-alpha3"]
                 [korma "0.3.0-RC5"]
                 [mysql/mysql-connector-java "5.1.21"]
                 [postgresql "9.0-801.jdbc4"]
                 [org.xerial/sqlite-jdbc "3.6.16"]
                 [cheshire "5.3.0"]
                 [clj-time "0.6.0"]
                 [dire "0.5.1"]])

