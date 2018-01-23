(ns todo-quest.shared.util)

(defn complete? [quest] (= (:status quest) "completed"))
