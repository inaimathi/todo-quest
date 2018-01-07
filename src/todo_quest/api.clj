(ns todo-quest.api
  (:require
   [acknowledge.core :as handlers]

   [cheshire.core :as json]

   [todo-quest.auth.core :as auth]
   [todo-quest.model :as db]
   [todo-quest.util :as util]))

(defn -api
  ([body] (-api 200 body))
  ([status body]
   {:status status
    :headers {"Content-Type" "application/json"}
    :body (json/encode body)}))

(def not-logged-in (-api 400 {:error "not logged in"}))

(defn json-api! [uri f]
  (handlers/intern-handler-fn!
   uri (keyword (clojure.string/replace uri #"(/:.*?/|[/-]+)" "-"))
   #(if (auth/logged-in? %) (-api (f %)) not-logged-in)))

(defn classic-api!
  [uri f]
  (handlers/intern-handler-fn!
   uri (keyword (clojure.string/replace uri #"(/:.*?/|[/-]+)" "-"))
   #(if (auth/logged-in? %)
      (do (f %)
          {:status 307
           :headers {"Content-Type" "text/plain" "location" "/"}
           :body "Redirecting..."})
      not-logged-in)))

(handlers/intern-handler-fn! "/api/ping" :ping-handler (fn [_] (-api :pong)))

(json-api!
 "/api/task/new"
 #(db/add-task!
   (get-in % [:session :user])
   (dissoc (json/decode (get-in % [:params "task"])) :_id)))

(classic-api!
 "/api/classic/new-task"
 #(db/add-task! (get-in % [:session :user]) {:text (java.net.URLDecoder/decode (get-in % [:params "task-text"]))}))

(json-api!
 "/api/task/:task-id/complete"
 #(let [t (db/get-task (org.bson.types.ObjectId. (get-in % [:params "task-id"])))]
    (db/complete-task! (get-in % [:session :user]) t)))

(classic-api!
 "/api/classic/complete-task"
 #(let [t (db/get-task (org.bson.types.ObjectId. (get-in % [:params "task-id"])))]
    (db/complete-task! (get-in % [:session :user]) t)))

(json-api!
 "/api/task/:task-id/uncomplete"
 #(let [t (db/get-task (org.bson.types.ObjectId. (get-in % [:params "task-id"])))]
    (db/uncomplete-task! (get-in % [:session :user]) t)))

(classic-api!
 "/api/classic/uncomplete-task"
 #(let [t (db/get-task (org.bson.types.ObjectId. (get-in % [:params "task-id"])))]
    (db/uncomplete-task! (get-in % [:session :user]) t)))

(json-api!
 "/api/task/list"
 #(db/get-user-tasks (get-in % [:session :user])))
