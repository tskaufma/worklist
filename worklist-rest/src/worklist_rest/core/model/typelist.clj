(ns worklist-rest.core.model.typelist
    (:require [clojure.java.jdbc :as sql]
              [worklist-rest.core.util :as util]
              [worklist-rest.core.db :refer [db-connection]]))
  
(defn get-all [table ]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      [(str "select * from tl_" table)]
      (into [] results))))

(defn get-item [table id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      [(str "select * from tl_" table " where id = ?") id]
      (cond
        (empty? results) nil
        :else (first results)))))

(defn create-new [table doc]
  (let [id (util/uuid)]
    (sql/with-connection (db-connection)
      (let [item (assoc doc "id" id "updated" (util/now-ts))]
        (sql/insert-record (str "tl_" table) item)))
    (get-item table id)))

;
(defn update [table id doc]
  (sql/with-connection (db-connection)
    (let [item (dissoc doc "id" "created" "updated")]
      (sql/update-values (str "tl_" table) ["id=?" id] (assoc item "updated" (util/now-ts) ))))
  (get-item table id))

(defn delete [table id]
  (sql/with-connection (db-connection)
    (sql/delete-rows (str "tl_" table) ["id=?" id]))
  {:success true})

(defn make-typelist 
  "makes a map to work with the named typelist"
  [table-name]
  (let [table (if (keyword? table-name) (name table-name) (str table-name))]
    {
      :get-all (partial get-all table)
      :get (partial get-item table)
      :create-new (partial create-new table)
      :update (partial update table)
      :delete (partial delete table)  
    }))

(def priority (make-typelist :priority))
(doall (map (:create-new priority) [
    {:code "critical" :name "Critical"}
    {:code "high" :name "High"}
    {:code "medium" :name "Medium"}
    {:code "low" :name "Low"}]))

(def status (make-typelist :status))
(doall (map (:create-new status) [
    {:code "open" :name "Open"}
    {:code "inprogress" :name "In-Progress"}
    {:code "waiting" :name "Waiting"}
    {:code "resolved" :name "Resolved"}
    {:code "deployed" :name "Deployed"}
    {:code "closed" :name "Closed"}]))
  
(def resolution (make-typelist :resolution))
(doall (map (:create-new resolution) [
    {:code "fixed" :name "Fixed"}
    {:code "rejected" :name "Rejected"}
    {:code "asdesigned" :name "As Designed"}
    {:code "wontfix" :name "Won't Fix"}
    {:code "cantreproduce" :name "Cannot Reproduce"}]))

(defn expand-typelist [typelist entity]
  "Gets the map representing the typelist value from the given entity"
  (get-item (name typelist) ((keyword typelist) entity)))

(defn tl-map [typelists entity]
  "creates a sequence of typelist, typelist value pairs for this entity"
  (map (fn [tl] (vector tl (expand-typelist tl entity))) typelists))

(defn wrap-typelists [typelists entity]
  "creates an entity which has the given typelist ids replaced by typelist value maps"
  (reduce (fn [e [k v]] (assoc e k v)) entity (tl-map typelists entity)))

