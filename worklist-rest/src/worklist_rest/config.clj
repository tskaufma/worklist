(ns worklist-rest.config
    (:require [environ.core :refer [env]]))

(def db-config
  {:classname "org.h2.Driver"
   :subprotocol "h2"
   :subname "h2/tasks;MODE=Oracle"
   :user ""
   :password ""})

(def db-config-new
    {:classname (env :database-driver)
     :subname (env :database-url)
     :user (env :database-user)
     :password (env :database-password)})
