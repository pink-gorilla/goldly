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
  :profiles {:cljs {:dependencies [[org.clojure/clojurescript "1.10.773"]
                                   [thheller/shadow-cljs "2.8.94"] ; 106
                                   [thi.ng/strf "0.2.2"]
                                   [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]
                                   [check "0.1.0-SNAPSHOT"]
                                   [funcool/promesa "4.0.2"]
                                   [paprika "0.1.3-SNAPSHOT"]
                                   [borkdude/sci "0.0.13-alpha.17"]
                                   [compliment "0.4.0-SNAPSHOT"]
                                   [rewrite-cljs "0.4.4"]
                                   [org.rksm/suitable "0.3.2"  :exclusions [org.clojure/clojurescript]]
                                   [cider/orchard "0.5.8"]
                                   [etaoin "0.3.6"]
                                   [reagent "0.10.0"]
                                   [re-frame "0.12.0"]
                                   [com.taoensso/timbre "4.10.0"]  ; clojurescript logging
                                   [com.taoensso/encore "2.119.0"]
                                   [com.taoensso/sente "1.15.0"] ;  websocket
                                   [clj-commons/secretary "1.2.4"]   ; client side routing - TODO: Should likely be replaced by jux/bidi
                                   [org.pinkgorilla/gorilla-ui "0.1.42"]]}


             :demo
             {:source-paths ["profiles/demo/src"]}


             :dev
             {:dependencies [[clj-kondo "2020.05.09"]]
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
            ["with-profile" "+demo" "run" "-m" "demo.demo1"]

            "outdated" ^{:doc "Runs ancient"}
            ["with-profile" "+cljs" "ancient"]})
