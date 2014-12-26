(ns worklist-rest.core.model.user
    (:require [clojure.java.jdbc.deprecated :as sql]
              [worklist-rest.core.util :as util]
              [worklist-rest.core.db :refer [db-connection]]
              (cemerick.friend [workflows :as workflows]
                                 [credentials :as creds])))

(defn get-all-users []
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from users"]
      (into [] results))))

(defn get-user [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from users where id = ?" id]
      (cond
        (empty? results) {}
        :else (first results)))))

(defn create-new-user [doc]
  (let [id (util/uuid)]
    (sql/with-connection (db-connection)
      (let [user (assoc doc "id" id "updated" (util/now-ts))]
        (sql/insert-record :users user)))
    (get-user id)))

;
(defn update-user [id doc]
  (sql/with-connection (db-connection)
    (let [user (dissoc doc "id" "created" "updated")]
      (sql/update-values :users ["id=?" id] (assoc user "updated" (util/now-ts) ))))
  (get-user id))

(defn delete-user [id]
  (sql/with-connection (db-connection)
    (sql/delete-rows :users ["id=?" id]))
  {:success true})

(defn user-by-username [username]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from users where username = ?" username]
      (cond
        (empty? results) {}
        :else (assoc (first results) :roles #{ :worklist-rest.core.handler/user })))))
      
(defonce tk (create-new-user {:username "trevor.kaufman" :password (creds/hash-bcrypt "woof")}))
