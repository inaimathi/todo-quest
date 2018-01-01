(ns todo-quest.core
  (:require
   [org.httpkit.server :as server]
   [acknowledge.core :as handlers]

   [ring.middleware.session :refer [wrap-session]]

   [todo-quest.auth.core :as auth]
   [todo-quest.model :as db]
   [todo-quest.page :as pg]
   [todo-quest.util :as util]))

(handlers/intern-static! "/static/" (handlers/resources "public/"))

(handlers/intern-handler-fn!
 "/" :login-page
 (fn [req]
   (if (auth/logged-in? req)
     (let [user (get-in req [:session :user])]
       (util/ok
        (pg/pg
         [:p "Welcome, " (:name user) "!"]
         [:ul
          (map
           (fn [t] [:li {:style (str "text-decoration: " (if (:done? t) "line-through" "none") ";") } (:name t)])
           (db/get-user-tasks user))]
         [:script {:src "/static/js/main.js" :type "text/javascript" :charset "utf-8"}])))
     (util/ok
      (pg/pg
       [:p "Hello there!"]
       [:a {:href auth/github-auth-url} "Login with Github"])))))

(defn -main
  []
  (server/run-server
   (wrap-session handlers/routes-handler)
   {:port 3000}))
