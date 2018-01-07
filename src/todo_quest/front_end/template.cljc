(ns todo-quest.front-end.template)

(defn task
  [t]
  [:li {:class "task"}
   [:form {:action (if (:done? t) "/api/classic/uncomplete-task" "/api/classic/complete-task")}
    [:input {:type "hidden" :name "task-id" :value (:_id t) :class "btn btn-primary"}]
    [:div {:class "input-group mb-3"}
     [:input {:type "submit" :value (if (:done? t) "☑" "☐") :class "btn btn-primary check-uncheck"}]
     [:span {:class "input-group-text"
             :style (str "text-decoration: " (if (:done? t) "line-through" "none") ";") }
      (:text t)]]]])

(defn task-list
  [tasks & {:keys [class]}]
  [:ul {:class class} (map task tasks)])

(defn task-pane
  [tasks]
  (let [ts (group-by :done? tasks)]
    [:span
     (task-list (get ts false) :class "in-progress")
     (task-list (get ts true) :class "complete")]))
