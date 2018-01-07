(ns todo-quest.front-end.util)

(defn dom-loaded [fn]
  (.addEventListener js/document "DOMContentLoaded" fn))

(defn $get
  ([uri on-done] ($get uri {} on-done))
  ([uri params on-done]
   (-> js/$
       (.get uri (clj->js params))
       (.done #(on-done (js->clj % :keywordize-keys true))))))

(defn $get-json
  ([uri on-done] ($get-json uri {} on-done))
  ([uri params on-done]
   ($get uri params (fn [data] (on-done (js->clj data))))))

(defn $post
  ([uri on-done] ($post uri {} on-done))
  ([uri params on-done]
   (-> js/$
       (.post uri (clj->js params))
       (.done #(on-done (js->clj % :keywordize-keys true))))))
