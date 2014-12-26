(ns worklist-rest.core.model.person
    (:require [clojure.java.jdbc.deprecated :as sql]
              [worklist-rest.core.util :as util]
              [worklist-rest.core.db :refer [db-connection]]))

(defn get-all-people []
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from people"]
      (into [] results))))

(defn get-person [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from people where id = ?" id]
      (cond
        (empty? results) {}
        :else (first results)))))

(defn create-new-person [doc]
  (let [id (util/uuid)]
    (sql/with-connection (db-connection)
      (let [person (assoc doc "id" id "updated" (util/now-ts))]
        (sql/insert-record :people person)))
    (get-person id)))

;
(defn update-person [id doc]
  (sql/with-connection (db-connection)
    (let [person (dissoc doc "id" "created" "updated")]
      (sql/update-values :people ["id=?" id] (assoc person "updated" (util/now-ts) ))))
  (get-person id))

(defn delete-person [id]
  (sql/with-connection (db-connection)
    (sql/delete-rows :people ["id=?" id]))
  {:success true})
