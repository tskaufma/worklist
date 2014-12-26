(ns lobos.config
    (:require [worklist-rest.core.db :as db]))

(open-global db/db-config)
