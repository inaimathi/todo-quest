(ns todo-quest.shared.template)

(defn quest
  [t]
  [:li {:class "quest"}
   [:span {:class "quest-controls"}
    (if (:done? t)
      [:a {:class "btn btn-warning status-change uncheck" :href (str "/api/classic/uncomplete-quest?quest-id=" (:_id t))} "☑"]
      [:a {:class "btn btn-success status-change check" :href (str "/api/classic/complete-quest?quest-id=" (:_id t))} "☐"])

    (if (not (:done? t))
      [:a {:class "btn btn-danger status-change fail" :href (str "/api/classic/fail-quest?quest-id=" (:_id t))} "☠"])]
   [:span {:class "quest-text"
           :style (str "text-decoration: " (if (:done? t) "line-through" "none") ";") }
    (:text t)]])

(defn quest-list
  [quests & {:keys [class title]}]
  [:ul
   {:class (str "quest-list " class)}
   (when title [:li {:class "quest-list-title"} [:h1 title]])
   (map quest quests)])

(defn quest-pane
  [quests]
  (let [ts (group-by :done? quests)]
    [:span
     (quest-list (get ts false) :class "in-progress" :title "In Progress")
     (quest-list (get ts true) :class "complete" :title "Complete")]))
