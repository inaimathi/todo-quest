(ns todo-quest.page
  (:require [hiccup.page :as html]))

(defn pg [& body]
  (html/html5
   {:lang "en"}
   [:head
    [:title "ToDo Quest"]
    [:link {:href "/static/css/bootstrap.min.css" :rel "stylesheet"}]
    [:link {:href "/static/css/main.css" :rel "stylesheet"}]]
   [:body body]))
