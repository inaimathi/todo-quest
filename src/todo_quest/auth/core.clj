(ns todo-quest.auth.core
  (:require
   [org.httpkit.client :as client]
   [acknowledge.core :as handlers]

   [cheshire.core :as json]

   [oauth.github :as github]

   [todo-quest.util :as util]
   [todo-quest.model :as db]
   [todo-quest.page :as pg]))

(def github-key "00682748f4dccf1bb529")
(def github-secret "7ef2651021e2df1ee86de4841ad06a4e5c31957d")
(def github-auth-url (github/oauth-authorization-url github-key nil))

(handlers/intern-handler-fn!
 "/oauth/log-out" :log-out
 (fn [req]
   {:status 307
    :session {}
    :headers {"Content-Type" "text/plain" "location" "/"}
    :body "Logging out..."}))

(handlers/intern-handler-fn!
 "/oauth/github/callback" :callback
 (fn [req]
   {:status 200
    :headers {"Content-Type" "text/html; charset=utf-8"}
    :body (pg/pg
           [:p "Got callback!"]
           [:pre (str req)])}))

(handlers/intern-handler-fn!
 "/oauth/github/authorization" :authorization
 (fn [req]
   (let [res (client/post
              "https://github.com/login/oauth/access_token"
              {:as :text
               :form-params {"client_id" github-key
                             "client_secret" github-secret
                             "code" (get-in req [:params "code"])}})]
     (if-let [access-token (get (handlers/parse-params (:body @res)) "access_token")]
       (let [res (client/get "https://api.github.com/user" {:as :text :query-params {"access_token" access-token}})
             user (json/parse-string (:body @res))]
         (println "GOT THE USER" user)
         (if-let [name (get user "login")]
           {:status 301
            :session {:user (db/get-or-add-user!
                             {:source :github
                              :role :user
                              :access-token access-token
                              :name name})}
            :headers {"Content-Type" "text/plain" "location" "/"}
            :body "You've been logged in! :D"}
           (util/ok
            (pg/pg
             [:p "Got auth and access token!"]
             [:pre (str req)]
             [:p "BUT THEN IT FUCKING FAILED"]
             [:pre (str @res)]))))
       (util/ok
        (pg/pg
         [:p "Got auth redirect!"]
         [:pre (str req)]
         [:p "BUT IT FUCKING FAILED"]
         [:pre (str @res)]))))))

(defn logged-in? [req]
  (not (not (get-in req [:session :user :name]))))

(defn logged-in-handler!
  [route name f]
  (handlers/intern-handler-fn!
   route name
   (fn [req]
     (if (logged-in? req)
       (f req)
       {:status 307
        :headers {"Content-Type" "text/plain" "location" "/"}
        :body "You have to log in first..."}))))
