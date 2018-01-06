(ns todo-quest.front-end.core
  (:require cljsjs.jquery
            [crate.core :as crate]

            [todo-quest.front-end.util :as util]
            [todo-quest.front-end.template :as tmpl]))

(def dom-elem (atom nil))

(defn render-task-list!
  [dom-elem]
  (util/$get
   "/api/task/list"
   (fn [data]
     (.log js/console "Woo!" dom-elem data)
     (.html dom-elem (crate/html (tmpl/task-list data))))))

(defn add-task! [task-text]
  (util/$post
   "/api/task/new"
   {:task {:text task-text}}
   (fn [data]
     (.log js/console "ADDED NEW TASK!")
     (render-task-list! @dom-elem))))

(defn initialize-todo-quest!
  []
  (render-task-list! @dom-elem))

(util/dom-loaded
 #(do (reset! dom-elem (js/$ "#todo-quest"))
      (initialize-todo-quest!)))
