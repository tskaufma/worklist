(ns worklist-rest.core.model.task
    (:require [clojure.java.jdbc :as sql]
              [worklist-rest.core.util :as util]
              [worklist-rest.core.db :refer [db-connection]]))

(defn get-all-tasks []
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from tasks"]
      (into [] results))))

(defn get-task [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from tasks where id = ?" id]
      (cond
        (empty? results) {}
        :else (first results)))))

(defn task-max-key []
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select max(key) as maxkey from tasks"]
      (cond
        (empty? results) 0
        :else (let [maxKey (:maxkey (first results))]
                   (clojure.pprint/pprint maxKey)
                   (cond (nil? maxKey) 0 :else maxKey))))))

(defn create-new-task [doc]
  (let [id (util/uuid)]
    (sql/with-connection (db-connection)
      (let [maxKey (task-max-key)
            task (dissoc (assoc doc "id" id "key" (inc maxKey) "updated" (util/now-ts) "project_id" (get-in doc ["project" "id"])) "project")]
        (sql/insert-record :tasks task)))
    (get-task id)))

;
(defn update-task [id doc]
  (sql/with-connection (db-connection)
    (let [task (dissoc doc "id" "created" "updated" "key" "project")]
      (sql/update-values :tasks ["id=?" id] (assoc task "updated" (util/now-ts) ))))
  (get-task id))

(defn delete-task [id]
  (sql/with-connection (db-connection)
    (sql/delete-rows :tasks ["id=?" id]))
  {:success true})
