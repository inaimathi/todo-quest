(ns todo-quest.model
  (:require
   [monger.core :as mg]
   [monger.collection :as mc]))

(defn with-db [f] (let [db (mg/get-db (mg/connect) "todo-quest")] (f db)))

(defn add-task! [user task]
  (with-db #(mc/insert % "tasks" (assoc task :assigned-to (:_id user) :created-by (:_id user) :done? false :created (java.util.Date.)))))
(defn complete-task! [user task]
  (if (:done? task)
    task
    (with-db
      #(do (mc/update-by-id
            % "tasks" (:_id task)
            (assoc task :done? true :completed-by (:_id user) :completed (java.util.Date.)))
           (first (mc/find-maps % "tasks" {:_id (:_id task)}))))))

(defn user->key [user] (str (name (:source user)) "::" (:name user)))

(defn get-user [user] (with-db #(first (mc/find-maps % "users" {:user-keys (user->key user)}))))
(defn add-user! [user]
  (let [u (update user :user-keys #(vec (concat % [(user->key user)])))]
    (with-db #(mc/insert-and-return % "users" u))))
(defn get-or-add-user! [user]
  (or (get-user user) (add-user! user)))

(defn get-tasks-matching [query] (with-db #(mc/find-maps % "tasks" query)))
(defn get-task [task-id] (first (get-tasks-matching {:_id task-id})))
(defn get-user-tasks [user] (get-tasks-matching {:created-by (:_id user)}))

(defn user-xp [user]
  (* 20 (count (get-tasks-matching {:completed-by (:_id user)}))))
(defn user-level [user]
  (Math/round (Math/floor (* 0.4 (Math/sqrt (user-xp user))))))
