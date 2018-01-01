(ns todo-quest.api
  (:require
   [acknowledge.core :as handlers]

   [cheshire.core :as json]

   [todo-quest.auth.core :as auth]
   [todo-quest.model :as db]
   [todo-quest.util :as util]))

(def not-logged-in
  {:status 400
   :headers {"Content-Type" "application/json"}
   :body (json/encode {:error "not logged in"})})

(handlers/intern-handler-fn!
 "/api/task" :add-task
 (fn [req]
   (if (auth/logged-in? req)
     (db/add-task! (get-in req [:session :user]) (dissoc (json/decode (get-in req [:params "task"])) :_id))
     not-logged-in)))

(handlers/intern-handler-fn!
 "/api/task/:task-id/complete" :complete-task
 (fn [req]
   (if (auth/logged-in? req)
     (let [t (db/get-task (org.bson.types.ObjectId. (get-in req [:params "task-id"])))]
       (db/complete-task! (get-in req [:session :user]) t))
     not-logged-in)))
