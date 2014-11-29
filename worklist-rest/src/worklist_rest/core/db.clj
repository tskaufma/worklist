(ns worklist-rest.core.db
    (:import com.mchange.v2.c3p0.ComboPooledDataSource)
    (:require [clojure.java.jdbc :as sql]
              [worklist-rest.config :refer [db-config]]
              [clojure.data.json :as json]))

(defn pool
  [config]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname config))
               (.setJdbcUrl (str "jdbc:" (:subprotocol config) ":" (:subname config)))
               (.setUser (:user config))
               (.setPassword (:password config))
               (.setMaxPoolSize 6)
               (.setMinPoolSize 1)
               (.setInitialPoolSize 1))]
    {:datasource cpds}))

(def pooled-db (delay (pool db-config)))

(defn db-connection [] @pooled-db)

; in-memory db, create table each time
(sql/with-connection (db-connection)
  (sql/create-table :tasks [:id "varchar(256)" "primary key"]
                               [:key :int]
                               [:title "varchar(1024)"]
                               [:text :varchar]
                               [:assignee :varchar]
                               [:priority :varchar]
                               [:status :varchar]
                               [:resolution :varchar]
                               [:project-id "varchar(256)"]
                               [:created :timestamp "DEFAULT CURRENT_TIMESTAMP"]
                               [:updated :timestamp]))


(sql/with-connection (db-connection)
  (sql/create-table :projects [:id "varchar(256)" "primary key"]
                               [:name "varchar(1024)"]
                               [:key "varchar(4)"]
                               [:description :varchar]
                               [:created :timestamp "DEFAULT CURRENT_TIMESTAMP"]
                               [:updated :timestamp]))


(extend-type java.sql.Timestamp
  json/JSONWriter
  (-write [date out]
    (json/-write (.getTime date) out)))


