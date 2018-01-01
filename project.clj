(defproject todo-quest "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljsbuild "1.1.6"]]
  :hooks [leiningen.cljsbuild]
  :dependencies [[org.clojure/clojure "1.8.0"]

                 [acknowledge "0.2.2-SNAPSHOT"]
                 [http-kit "2.1.18"]
                 [com.novemberain/monger "3.1.0"]

                 [hiccup "1.0.5"]
                 [cheshire "5.8.0"]

                 [oauth-clj "0.1.15"]

                 [cljsjs/jquery "3.2.1-0"]]
  :cljsbuild {:builds [{:source-paths ["src/todo_quest/front_end"]
                        :compiler {:output-to "resources/public/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true}
                        :jar true}]}
  :main todo-quest.core
  :aot [todo-quest.core])
