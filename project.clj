(defproject rhythm "0.1.1-SNAPSHOT"
  :description "A ClojureScript library handling states and actions for event-driven programming"
  :url "https://github.com/federkasten/rhythm"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :plugins [[lein-cljsbuild "1.0.3"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.6.0"]
                                  [org.clojure/clojurescript "0.0-2261"]]}}
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
