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

(defn object-id [str] (org.bson.types.ObjectId. str))

(defn optionally
  ([req param-name] (optionally req param-name identity))
  ([req param-name transform]
   (if-let [val (get-in req [:params param-name])]
     (if (not (empty? val)) (transform val)))))

(handlers/intern-handler-fn! "/api/ping" :ping-handler (fn [_] (-api :pong)))

(json-api!
 "/api/quest/new"
 #(db/add-quest!
   (get-in % [:session :user])
   {:text (get-in % [:params "text"])
    :parent (optionally % "parent" object-id)}))

(classic-api!
 "/api/classic/new-quest"
 #(db/add-quest!
   (get-in % [:session :user])
   {:text (java.net.URLDecoder/decode (get-in % [:params "text"]))
    :parent (optionally % "parent" object-id)}))

(json-api!
 "/api/quest/:quest-id/edit"
 #(db/update-quest!
   (get-in % [:session :user])
   {:_id (object-id (get-in % [:params "quest-id"]))
    :text (get-in % [:params "text"])}))

(classic-api!
 "/api/classic/edit-quest"
 #(db/add-quest!
   (get-in % [:session :user])
   {:_id (object-id (get-in % [:params "quest-id"]))
    :text (java.net.URLDecoder/decode (get-in % [:params "text"]))}))

(json-api!
 "/api/quest/:quest-id/complete"
 #(let [t (db/get-quest (object-id (get-in % [:params "quest-id"])))]
    (db/complete-quest! (get-in % [:session :user]) t)))

(classic-api!
 "/api/classic/complete-quest"
 #(let [t (db/get-quest (object-id (get-in % [:params "quest-id"])))]
    (db/complete-quest! (get-in % [:session :user]) t)))

(json-api!
 "/api/quest/:quest-id/uncomplete"
 #(let [t (db/get-quest (object-id (get-in % [:params "quest-id"])))]
    (db/uncomplete-quest! (get-in % [:session :user]) t)))

(classic-api!
 "/api/classic/uncomplete-quest"
 #(let [t (db/get-quest (object-id (get-in % [:params "quest-id"])))]
    (db/uncomplete-quest! (get-in % [:session :user]) t)))

(json-api!
 "/api/quest/:quest-id/fail"
 #(let [t (db/get-quest (object-id (get-in % [:params "quest-id"])))]
    (db/fail-quest! (get-in % [:session :user]) t)))

(classic-api!
 "/api/classic/fail-quest"
 #(let [t (db/get-quest (object-id (get-in % [:params "quest-id"])))]
    (db/fail-quest! (get-in % [:session :user]) t)))


(json-api!
 "/api/quest/list"
 #(db/get-user-quests (get-in % [:session :user])))
