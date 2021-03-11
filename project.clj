(defproject org.clojars.quoll/zuko "0.4.5"
  :description "Threatgrid library for common graph database functionality"
  :url "https://github.com/threatgrid/zuko"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.2"]
                 [org.clojars.quoll/naga-store "0.5.2"]
                 [prismatic/schema "1.1.12"]
                 [cheshire "5.10.0"]]
  :plugins [[lein-cljsbuild "1.1.8"]]
  :profiles {
    :dev {
      :dependencies [[org.clojure/clojurescript "1.10.773"]
                     [org.clojars.quoll/qtest "0.1.1"]
                     [org.clojars.quoll/asami "1.2.13"]]}}
  :cljsbuild {
    :builds {
      :dev
      {:source-paths ["src"]
       :compiler {
         :output-to "out/zuko/core.js"
         :optimizations :simple
         :pretty-print true}
       :dependencies [[org.clojars.quoll/asami "1.2.13"]]}
      :test
      {:source-paths ["src" "test"]
       :compiler {
         :output-to "out/zuko/test_memory.js"
         :optimizations :simple
         :pretty-print true}
       :dependencies [[org.clojars.quoll/asami "1.2.13"]]}
      }
    :test-commands {
      "unit" ["node" "out/zuko/test_memory.js"]}})
