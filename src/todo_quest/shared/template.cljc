(ns todo-quest.shared.template)

(defn task
  [t]
  [:li {:class "task"}
   (if (:done? t)
     [:a {:class "status-change uncheck" :href (str "/api/classic/uncomplete-task?task-id=" (:_id t))} "☑"]
     [:a {:class "status-change check" :href (str "/api/classic/complete-task?task-id=" (:_id t))} "☐"])

   (if (not (:done? t))
     [:a {:class "status-change fail" :href (str "/api/classic/fail-task?task-id=" (:_id t))} "☠"])
   [:span {:class "input-group-text"
           :style (str "text-decoration: " (if (:done? t) "line-through" "none") ";") }
    (:text t)]])

(defn task-list
  [tasks & {:keys [class]}]
  [:ul {:class (str "task-list " class)} (map task tasks)])

(defn task-pane
  [tasks]
  (let [ts (group-by :done? tasks)]
    [:span
     (task-list (get ts false) :class "in-progress")
     (task-list (get ts true) :class "complete")]))
