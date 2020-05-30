(defproject where-is-my-money "0.1.0-SNAPSHOT"
  :source-paths ["src" "test"]
  :dependencies [[org.clojure/clojure       "1.10.1"]
                 [org.clojure/clojurescript "1.10.597"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]
                 [thheller/shadow-cljs      "2.8.83"]]

  :plugins      [[lein-shadow          "0.1.7"]]

  :clean-targets ^{:protect false} [:target-path
                                    "shadow-cljs.edn"
                                    "package.json"
                                    "package-lock.json"
                                    "build-clj"]
  :shadow-cljs {:nrepl  {:port 8777}
                :builds {:test {:target :node-test
                                :output-to "build/test.js"
                                :autorun true
                                :main money.test-runner/main}}}

  :aliases {"auto-test" ["shadow" "watch" "test"]})
