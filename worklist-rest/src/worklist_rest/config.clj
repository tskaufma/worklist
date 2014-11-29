(ns worklist-rest.config
)

(def db-config
  {:classname "org.h2.Driver"
   :subprotocol "h2"
   :subname "mem:tasks;MODE=Oracle"
   :user ""
   :password ""})