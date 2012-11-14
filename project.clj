(defproject dibble "0.1.1"
  :description "A Clojure library for seeding databases"
  :url "https://github.com/MichaelDrogalis/dibble"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"stuart" "http://stuartsierra.com/maven2"}  
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [midje "1.4.0"]
                 [com.stuartsierra/lazytest "1.2.3"]
                 [korma "0.3.0-beta9"]
                 [mysql/mysql-connector-java "5.1.21"]
                 [org.clojure/math.numeric-tower "0.0.1"]]
  :plugins [[lein-midje "2.0.0-SNAPSHOT"]])

