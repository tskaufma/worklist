(ns lobos.config
    (:require [worklist-rest.core.db :as db]))

(open-global (db/database-connection))
