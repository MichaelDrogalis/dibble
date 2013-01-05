(defproject dibble "0.2.0-beta1"
  :description "A Clojure library for seeding databases"
  :url "https://github.com/MichaelDrogalis/dibble"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"stuart" "http://stuartsierra.com/maven2"}  
  :dependencies [[org.clojure/clojure "1.5.0-RC1"]
                 [midje "1.4.0"]
                 [com.stuartsierra/lazytest "1.2.3"]
                 [korma "0.3.0-beta11"]
                 [mysql/mysql-connector-java "5.1.21"]
                 [postgresql "9.0-801.jdbc4"]
                 [org.xerial/sqlite-jdbc "3.6.16"]
                 [cheshire "5.0.0"]
                 [clj-time "0.4.4"]
                 [dire "0.2.0"]]
  :plugins [[lein-midje "2.0.4"]])

