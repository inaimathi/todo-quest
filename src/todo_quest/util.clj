(ns todo-quest.util)

(defn ok
  ([body] (ok {} body))
  ([{:keys [session]} body]
   (let [base {:status 200
               :headers {"Content-Type" "text/html; charset=utf-8"}
               :body body}]
     (if session
       (assoc base :session session)
       base))))
