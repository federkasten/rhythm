(defproject rhythm "0.1.1-SNAPSHOT"
  :description "A ClojureScript library handling states and events"
  :plugins [[lein-cljsbuild "1.0.3"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.6.0"]
                                  [org.clojure/clojurescript "0.0-2202"]]}}
  :source-paths ["src"]
  :cljsbuild {:builds
              {:dev
               {:id "dev"
                :source-paths ["src"]
                :jar true
                :compiler {:output-to "target/gen/rhythm_dev.js"
                           :optimizations :whitespace
                           :pretty-print true}}
               :prod
               {:id "prod"
                :source-paths ["src"]
                :jar true
                :compiler {:output-to "target/gen/rhythm.js"
                           :optimizations :advanced
                           :pretty-print true}}}})
