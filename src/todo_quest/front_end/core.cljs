(ns todo-quest.front-end.core
  (:require cljsjs.jquery
            [crate.core :as crate]

            [todo-quest.front-end.util :as util]
            [todo-quest.shared.template :as tmpl]))

(def dom-elem (atom nil))

(defn render-quest-list!
  [dom-elem]
  (util/$get
   "/api/quest/list"
   (fn [data]
     (.log js/console "Woo!" dom-elem data)
     (.html dom-elem (crate/html (tmpl/quest-pane data))))))

(defn add-quest! [quest-text]
  (util/$post
   "/api/quest/new"
   {:quest {:text quest-text}}
   (fn [data] (render-quest-list! @dom-elem))))

(defn initialize-todo-quest!
  []
  (render-quest-list! @dom-elem))

(util/dom-loaded
 #(do (reset! dom-elem (js/$ "#todo-quest"))
      (initialize-todo-quest!)))
