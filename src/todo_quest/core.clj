(ns todo-quest.core
  (:require
   [org.httpkit.server :as server]
   [acknowledge.core :as handlers]

   [ring.middleware.session :refer [wrap-session]]

   [todo-quest.auth.core :as auth]
   [todo-quest.model :as db]
   [todo-quest.page :as pg]
   [todo-quest.util :as util]
   [todo-quest.api :as api]

   [todo-quest.shared.template :as tmpl]
   [todo-quest.shared.xp :as xp]))

(handlers/intern-static! "/static/" (handlers/resources "public/"))

(handlers/intern-handler-fn!
 "/" :main-page
 (fn [req]
   (if (auth/logged-in? req)
     (let [user (get-in req [:session :user])]
       (util/ok
        (pg/pg
         [:div {:id "avatar"}
          [:ul
           [:li "Name: " (:name user)]
           [:li "XP: " (db/user-xp user) "/" (xp/level->xp (inc (db/user-level user)))]
           [:li "Level: " (db/user-level user)]]]

         [:div {:id "toolbar"}
          [:form {:action "/api/classic/new-quest"}
           [:button {:type "submit" :class "btn btn-primary new-quest-button"} "+"]
           [:input {:type "text" :name "text" :class "form-control new-quest-name"}]]]

         [:div {:id "todo-quest"}
          (tmpl/quest-pane (db/get-user-quests user))]

         [:a {:href "/oauth/log-out"} "Log Out"]
         [:script {:src "/static/js/main.js" :type "text/javascript" :charset "utf-8"}])))
     (util/ok
      (pg/pg
       [:p "Hello there!"]
       [:a {:href auth/github-auth-url} "Login with Github"])))))

(defn -main
  []
  (cheshire.generate/add-encoder
   org.bson.types.ObjectId
   (fn [c jsonGenerator]
     (.writeString jsonGenerator (str c))))
  (server/run-server
   (->> handlers/routes-handler
        wrap-session)
   {:port 3000}))
