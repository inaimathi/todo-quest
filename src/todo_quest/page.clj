(ns todo-quest.page
  (:require [hiccup.page :as html]))

(defn pg [& body]
  (html/html5
   {:lang "en"}
   [:head [:title "ToDo Quest"]]
   [:body body]))
