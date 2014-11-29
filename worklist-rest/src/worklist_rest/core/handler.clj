(ns worklist-rest.core.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
                [compojure.route :as route]
                [ring.middleware.params :refer [wrap-params]]
                [liberator.core :refer [resource defresource]]
                [worklist-rest.core.model.task :as task]
                [worklist-rest.core.model.project :as proj]
                [worklist-rest.core.model.util :refer [parse-json]]))

(defresource tasks-resource
             :allowed-methods [:post :get]
             :available-media-types ["application/json"]
             :handle-ok (fn [_] (task/get-all-tasks))
             :malformed? #(parse-json % ::data)
             :post! (fn [ctx]
                        (let [body (get ctx ::data)
                              task (task/create-new-task body)]
                            (clojure.pprint/pprint task)
                            {::id (get-in task [:id])}))
             :post-redirect? true
             :location (fn [ctx] (format "/api/task/%s" (::id ctx)))
             :handle-malformed (fn [ctx] 
                                   {:error (get ctx :message)}))

(defresource task-resource [id]
             :allowed-methods [:get :put]
             :can-put-to-missing? false
             :available-media-types ["application/json"]
             :handle-ok (fn [_] (task/get-task id))
             :malformed? #(parse-json % ::data)
             :new? false
             :respond-with-entity? true
             :put! #(task/update-task id (::data %)))
         
(defresource projects-resource
             :allowed-methods [:post :get]
             :available-media-types ["application/json"]
             :handle-ok (fn [_] (proj/get-all-projects))
             :malformed? #(parse-json % ::data)
             :post! (fn [ctx]
                        (let [body (get ctx ::data)
                              project (proj/create-new-project body)]
                            (clojure.pprint/pprint project)
                            {::id (get-in project [:id])}))
             :post-redirect? true
             :location (fn [ctx] (format "/api/project/%s" (::id ctx)))
             :handle-malformed (fn [ctx] 
                                   {:error (get ctx :message)}))

(defresource project-resource [id]
             :allowed-methods [:get :put]
             :can-put-to-missing? false
             :available-media-types ["application/json"]
             :handle-ok (fn [_] (proj/get-project id))
             :malformed? #(parse-json % ::data)
             :new? false
             :respond-with-entity? true
             :put! #(proj/update-project id (::data %)))
             

(defroutes app-routes
  (GET "/test" [] "TK is writing clojure code! ")
  (context "/api" [] (defroutes api-tasks
                                (ANY "/tasks" [] tasks-resource)
                                (ANY "/task/:id" [id] (task-resource id))
                                (ANY "/projects" [] projects-resource)
                                (ANY "/project/:id" [id] (project-resource id))))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      wrap-params))
