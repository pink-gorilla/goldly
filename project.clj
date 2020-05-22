(defproject org.pinkgorilla/shiny-clj "0.0.1-SNAPSHOT"
  :description "PinkGorilla reactive ui"
  :license {:name "MIT"}
  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/release_username
                                     :password :env/release_password
                                     :sign-releases false}]]
  :min-lein-version "2.9.1"
  :min-java-version "1.11"

  :release-tasks [["vcs" "assert-committed"]
                  ["bump-version" "release"]
                  ["vcs" "commit" "Release %s"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["deploy"]
                  ["bump-version"]
                  ["vcs" "commit" "Begin %s"]
                  ["vcs" "push"]]

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.logging "0.4.0"]
                 [ring/ring-core "1.6.3"]
                 [ring-cors "0.1.13"]
                 [ring/ring-defaults "0.3.2"
                  :exclusions [javax.servlet/servlet-api]]
                 ;; [ring.middleware.logger "0.5.0"]
                 [ring-middleware-format "0.7.4"]
                 [ring/ring-json "0.5.0"]
                 [ring-cljsjs "0.1.0"]
                 [bk/ring-gzip "0.3.0"] ; from oz
                 [http-kit "2.3.0"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.4"]
                 [com.taoensso/encore "2.119.0"]
                 [com.taoensso/sente "1.15.0"] ; websocket
                 [org.clojure/data.json "1.0.0"]]
  :source-paths ["src"]
  :resource-paths ["resources"]


  ;:repl-options {:init-ns ta.model.single}
  :profiles {:dev
             {:dependencies [[clj-kondo "2019.11.23"]]
              :plugins      [[lein-cljfmt "0.6.6"]
                             [lein-cloverage "1.1.2"]]
              :aliases      {"clj-kondo" ["run" "-m" "clj-kondo.main"]}
              :cloverage    {:codecov? true
                                  ;; In case we want to exclude stuff
                                  ;; :ns-exclude-regex [#".*util.instrument"]
                                  ;; :test-ns-regex [#"^((?!debug-integration-test).)*$$"]
                             }
                   ;; TODO : Make cljfmt really nice : https://devhub.io/repos/bbatsov-cljfmt
              :cljfmt       {:indents {as->                [[:inner 0]]
                                       with-debug-bindings [[:inner 0]]
                                       merge-meta          [[:inner 0]]
                                       try-if-let          [[:block 1]]}}}}

  :plugins [[lein-shell "0.5.0"]
            [lein-ancient "0.6.15"]
            [min-java-version "0.1.0"]]

  :aliases {"bump-version"
            ["change" "version" "leiningen.release/bump-version"]

            "lint" ^{:doc "Runs code linter"}
            ["clj-kondo" "--lint" "src"]

            "shadow-compile"  ^{:doc "compiles UI"}
            ["shell" "shadow-cljs" "compile" "web"]

            "demo" ^{:doc "Runs demo"}
            ["run" "-m" "shiny.demo1"]})
