(ns todo-quest.shared.xp)

(defn xp->level [xp] (Math/round (Math/floor (* 0.4 (Math/sqrt xp)))))
(def level->xp-map (->> (range 4001)
                        (map #(* 10 %))
                        (map (fn [xp] [(xp->level xp) xp]))
                        (partition-by first)
                        (map first)
                        (into {})))
(defn level->xp [level] (get level->xp-map level))