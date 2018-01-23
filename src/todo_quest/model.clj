(ns todo-quest.model
  (:require
   [monger.core :as mg]
   [monger.collection :as mc]

   [todo-quest.shared.xp :as xp]
   [todo-quest.shared.util :as util :refer [complete?]]))

(defn with-db [f] (let [db (mg/get-db (mg/connect) "todo-quest")] (f db)))

(defn add-quest! [user quest]
  (with-db #(mc/insert % "quests" (assoc quest :assigned-to (:_id user) :created-by (:_id user) :status :started :created (java.util.Date.)))))
(defn complete-quest! [user quest]
  (if (complete? quest)
    quest
    (with-db
      #(do (mc/update-by-id
            % "quests" (:_id quest)
            (assoc quest :status :completed :completed-by (:_id user) :completed (java.util.Date.)))
           (first (mc/find-maps % "quests" {:_id (:_id quest)}))))))

(defn update-quest!
  [user quest]
  (with-db
    #(let [q (first (mc/find-maps % "quests" {:_id (:_id quest)}))]
       (when q
         (mc/update-by-id % "quests" (:_id quest) (merge quest q))
         (first (mc/find-maps % "quests" {:_id (:_id quest)}))))))

(defn uncomplete-quest! [user quest]
  (if (complete? quest)
    (update-quest! user (assoc quest :status :started))
    quest))

(defn fail-quest! [user quest] (update-quest! user (assoc quest :status :failed)))

(defn user->key [user] (str (name (:source user)) "::" (:name user)))

(defn get-quests-matching [query] (with-db #(mc/find-maps % "quests" query)))
(defn get-quest [quest-id] (first (get-quests-matching {:_id quest-id})))
(defn get-user-quests [user] (get-quests-matching {:created-by (:_id user)}))

(defn user-xp [user]
  (* 15 (count (get-quests-matching {:completed-by (:_id user)}))))
(defn user-level [user] (xp/xp->level (user-xp user)))

(defn get-user [user] (with-db #(first (mc/find-maps % "users" {:user-keys (user->key user)}))))
(defn add-user! [user]
  (let [u (update user :user-keys #(vec (concat % [(user->key user)])))]
    (with-db #(mc/insert-and-return % "users" u))))
(defn get-or-add-user! [user]
  (or (get-user user) (add-user! user)))
