(ns worklist-rest.core.handler-old
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:use compojure.core)
  (:use cheshire.core)
  (:use ring.util.response)
  (:require [compojure.handler :as handler]
                [ring.middleware.json :as middleware]
                [clojure.java.jdbc :as sql]
                [compojure.route :as route]
                [ring.middleware.params :refer [wrap-params]]
                [liberator.core :refer [resource defresource]]))

(def db-config
  {:classname "org.h2.Driver"
   :subprotocol "h2"
   :subname "mem:tasks"
   :user ""
   :password ""})

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
;(sql/with-connection (db-connection)
;  (sql/create-table :tasks [:id "varchar(256)" "primary key"]
;                               [:title "varchar(1024)"]
;                               [:text :varchar]))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn get-all-tasks []
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["select * from tasks"]
        (into [] results)))))

(defn get-task [id]
  (sql/with-connection (db-connection)
    (sql/with-query-results results
      ["select * from tasks where id = ?" id]
      (cond
        (empty? results) {:status 404}
        :else (response (first results))))))


(defn create-new-task [doc]
  (let [id (uuid)]
    (sql/with-connection (db-connection)
      (let [task (assoc doc "id" id)]
        (sql/insert-record :tasks task)))
    (get-task id)))

;
(defn update-task [id doc]
  (sql/with-connection (db-connection)
    (let [task (assoc doc "id" id)]
      (sql/update-values :tasks ["id=?" id] task)))
  (get-task id))

(defn delete-task [id]
  (sql/with-connection (db-connection)
    (sql/delete-rows :tasks ["id=?" id]))
  {:status 204})

;; Liberator Resources

;; convert the body to a reader. Useful for testing in the repl
;; where setting the body to a string is much simpler.
(defn body-as-string [ctx]
  (if-let [body (get-in ctx [:request :body])]
    (condp instance? body
      java.lang.String body
      (slurp body))))

;; For PUT and POST parse the body as json and store in the context
;; under the given key.
(defn parse-json [context key]
  (when (#{:put :post} (get-in context [:request :request-method]))
    (try
      (if-let [body (body-as-string context)]
        (let [data (parse-string body)]
          [false {key data}])
        {:message "No body" :representation {:media-type "application/json"}})
      (catch Exception e
        (.printStackTrace e)
        {:message (format "IOException: %s" (.getMessage e))  :representation {:media-type "application/json"}}))))

(defresource tasks-resource
             :allowed-methods [:post :get]
             :available-media-types ["application/json"]
             :handle-ok (fn [_] (get-all-tasks))
             :malformed? #(parse-json % ::data)
             :post! (fn [ctx]
                        (let [body (get ctx ::data)
                              task (create-new-task body)]
                            (clojure.pprint/pprint task)
                            {::id (get-in task [:body :id])}))
             :post-redirect? true
             :location (fn [ctx] (format "/api/task/%s" (::id ctx)))
             :handle-malformed (fn [ctx] 
                                   {:error (get ctx :message)}))

(defresource task-resource [id]
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :handle-ok (fn [_] (get-task id)))
             

(defroutes app-routes
  (GET "/" [] "TK is writing clojure code! ")
  (context "/tasks" [] (defroutes tasks-routes
                                      (GET "/" [] (get-all-tasks))
                                      (POST "/" {body :body} (create-new-task body))
                                      (context "/:id" [id] (defroutes task-routes
                                                                      (GET "/" [] (get-task id))
                                                                      (PUT "/" {body :body} (update-task id body))
                                                                      (DELETE "/" [] (delete-task id))))))
  (context "/api" [] (defroutes api-tasks
                                (ANY "/tasks" [] tasks-resource)
                                (ANY "/task/:id" [id] (task-resource id))))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      wrap-params))
