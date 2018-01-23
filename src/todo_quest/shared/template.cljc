(ns todo-quest.shared.template
  (:require [todo-quest.shared.util :as util]))

(defn quest
  [t]
  [:li {:class "quest"}
   [:span {:class "quest-controls"}
    (if (util/complete? t)
      [:a {:class "btn btn-warning status-change uncheck" :href (str "/api/classic/uncomplete-quest?quest-id=" (:_id t))} "☑"]
      [:a {:class "btn btn-success status-change check" :href (str "/api/classic/complete-quest?quest-id=" (:_id t))} "☐"])

    (if (not (util/complete? t))
      [:a {:class "btn btn-danger status-change fail" :href (str "/api/classic/fail-quest?quest-id=" (:_id t))} "☠"])]
   [:span {:class "quest-text"
           :style (str "text-decoration: " (if (util/complete? t) "line-through" "none") ";") }
    (:text t)]])

(defn quest-list
  [quests & {:keys [class title]}]
  (when (not (empty? quests))
    [:ul
     {:class (str "quest-list " class)}
     (when title [:li {:class "quest-list-title"} [:h1 title]])
     (map quest quests)]))

(defn quest-pane
  [quests]
  (let [ts (group-by :status quests)]
    [:span
     (quest-list (concat (get ts "started") (get ts nil)) :class "in-progress" :title "In Progress")
     (quest-list (get ts "completed") :class "complete" :title "Complete")
     (quest-list (get ts "failed") :class "failed" :title "Failed")]))
