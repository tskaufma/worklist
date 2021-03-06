(defproject worklist-rest "0.1.0-SNAPSHOT"
  :description "Rest Service for WorkList app"
  :url "http://tskaufma.ca"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.2.0"]
                 [ring/ring-json "0.1.2"]
                 [c3p0/c3p0 "0.9.1.2"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [com.h2database/h2 "1.3.168"]
                 [cheshire "4.0.3"]
                 [liberator "0.10.0"]
                 [com.cemerick/friend "0.2.1"]
                 [lobos "1.0.0-beta3"]
                 [environ "1.0.0"]]
  :plugins [[lein-ring "0.8.13"]
            [environ "1.0.0"]]
  :ring {:handler worklist-rest.core.handler/app}
  :env { :database-url "h2:mem:worklist" :database-driver "org.h2.Driver" }
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
