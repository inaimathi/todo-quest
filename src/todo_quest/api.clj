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
 "/api/quest/new"
 #(db/add-quest!
   (get-in % [:session :user])
   (dissoc (json/decode (get-in % [:params "quest"])) :_id)))

(classic-api!
 "/api/classic/new-quest"
 #(db/add-quest! (get-in % [:session :user]) {:text (java.net.URLDecoder/decode (get-in % [:params "quest-text"]))}))

(json-api!
 "/api/quest/:quest-id/complete"
 #(let [t (db/get-quest (org.bson.types.ObjectId. (get-in % [:params "quest-id"])))]
    (db/complete-quest! (get-in % [:session :user]) t)))

(classic-api!
 "/api/classic/complete-quest"
 #(let [t (db/get-quest (org.bson.types.ObjectId. (get-in % [:params "quest-id"])))]
    (db/complete-quest! (get-in % [:session :user]) t)))

(json-api!
 "/api/quest/:quest-id/uncomplete"
 #(let [t (db/get-quest (org.bson.types.ObjectId. (get-in % [:params "quest-id"])))]
    (db/uncomplete-quest! (get-in % [:session :user]) t)))

(classic-api!
 "/api/classic/uncomplete-quest"
 #(let [t (db/get-quest (org.bson.types.ObjectId. (get-in % [:params "quest-id"])))]
    (db/uncomplete-quest! (get-in % [:session :user]) t)))

(json-api!
 "/api/quest/list"
 #(db/get-user-quests (get-in % [:session :user])))
