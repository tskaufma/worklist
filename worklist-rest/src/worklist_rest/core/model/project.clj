(ns worklist-rest.core.model.project
    (:require [clojure.java.jdbc :as sql]
              [worklist-rest.core.util :as util]
              [worklist-rest.core.db :refer [db-connection]]))

(defn get-all-projects []
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from projects"]
      (into [] results))))

(defn get-project [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from projects where id = ?" id]
      (cond
        (empty? results) {}
        :else (first results)))))

(defn create-new-project [doc]
  (let [id (util/uuid)]
    (sql/with-connection (db-connection)
      (let [project (assoc doc "id" id "updated" (util/now-ts))]
        (sql/insert-record :projects project)))
    (get-project id)))

;
(defn update-project [id doc]
  (sql/with-connection (db-connection)
    (let [project (dissoc doc "id" "created" "updated")]
      (sql/update-values :projects ["id=?" id] (assoc project "updated" (util/now-ts) ))))
  (get-project id))

(defn delete-project [id]
  (sql/with-connection (db-connection)
    (sql/delete-rows :projects ["id=?" id]))
  {:success true})

(defn enrichTask [task]
  (if (:project_id task)
    (let [project (get-project (:project_id task))
          result (assoc task :project project)]
        (clojure.pprint/pprint result)
        result)
    task))

(defn unwrapTask [task]
  (if (get task "project")
      (let [project_id (get-in task ["project" "id"])
            result (assoc (dissoc task "project") "project_id" project_id)]
           (clojure.pprint/pprint result)
           result)
      task))
