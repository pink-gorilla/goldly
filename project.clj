(defproject org.pinkgorilla/goldly "0.0.5-SNAPSHOT"
  :description "reactive html ui with clj-cljs interop"
  :license {:name "MIT"}
  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/release_username
                                     :password :env/release_password
                                     :sign-releases false}]]
  :min-lein-version "2.9.3"
  :min-java-version "1.11"
  :jvm-opts ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/jul-factory"]

  :release-tasks [["vcs" "assert-committed"]
                  ["bump-version" "release"]
                  ["vcs" "commit" "Release %s"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["deploy"]
                  ["bump-version"]
                  ["vcs" "commit" "Begin %s"]
                  ["vcs" "push"]]

  :managed-dependencies [[org.clojure/core.async "1.2.603"]
                         [org.clojure/tools.logging "1.1.0"]
                         [org.clojure/core.memoize "1.0.236"]
                         ; libpythonclj fixes
                         [net.java.dev.jna/jna "5.5.0"]
                         [org.ow2.asm/asm "8.0.1"]]

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [thheller/shadow-cljs "2.10.4"]
                 [thheller/shadow-cljsjs "0.0.21"]
                 [ring/ring-core "1.8.1"]
                 [ring-cors "0.1.13"]
                 [ring/ring-defaults "0.3.2"
                  :exclusions [javax.servlet/servlet-api]]
                 ;; [ring.middleware.logger "0.5.0"]
                 [ring-middleware-format "0.7.4"]
                 [ring/ring-json "0.5.0"]
                 [ring-cljsjs "0.2.0"]
                 [bk/ring-gzip "0.3.0"] ; from oz
                 [http-kit "2.3.0"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [com.taoensso/timbre "4.10.0"]  ; clojurescript logging
                 [com.taoensso/encore "2.119.0"]
                 [com.taoensso/sente "1.15.0"
                  :exclusions [aleph
                               org.clojure/core.async
                               org.immutant
                               info.sunng/ring-jetty9-adapter]] ;  websocket
                 [org.clojure/data.json "1.0.0"]
                 [com.rpl/specter "1.1.3"]
                 ;[clj-commons/pomegranate "1.2.0"] ; add-dependency in clj kernel TODO : Replace pomegranate with tools alpha
                 ;ui dependencies (clj must serve resources):
                 [org.pinkgorilla/gorilla-renderable-ui "0.1.36"]
                 [org.pinkgorilla/gorilla-ui "0.1.67"
                  :exclusions [org.clojure/clojurescript]]
                 [org.pinkgorilla/gorilla-plot "0.9.12"
                  :exclusions [org.clojure/clojurescript]]]

  :source-paths ["src"]
  :resource-paths ["resources"]

  :profiles {:cljs {:dependencies [[org.clojure/clojurescript "1.10.773"]
                                   [thi.ng/strf "0.2.2"]
                                   [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]
                                   ; [check "0.1.0-SNAPSHOT"] ;mauricio test helper
                                   [funcool/promesa "4.0.2"]
                                   [paprika "0.1.3"] ; mauricio helper functions 
                                   [borkdude/sci "0.0.13-alpha.17"]
                                   ; [compliment "0.4.0-SNAPSHOT"] ; code completions
                                   [rewrite-cljs "0.4.4"]
                                   [org.rksm/suitable "0.3.2"  :exclusions [org.clojure/clojurescript]]
                                   [cider/orchard "0.5.8"]
                                   [etaoin "0.3.6"]
                                   [reagent "0.10.0"]
                                   [re-frame "0.12.0"]
                                   [clj-commons/secretary "1.2.4"]   ; client side routing - TODO: replace by jux/bidi ?
                                   ]}

             :dev {:source-paths ["profiles/dev/src" "test"]
                   :dependencies [[clj-kondo "2020.05.09"]]
                   :plugins      [[lein-cljfmt "0.6.6"]
                                  [lein-cloverage "1.1.2"]
                                  [lein-codox "0.10.7"]
                                  [lein-shell "0.5.0"]
                                  [lein-ancient "0.6.15"]
                                  [min-java-version "0.1.0"]]
                   :aliases      {"clj-kondo"
                                  ["run" "-m" "clj-kondo.main"]

                                  "lint" ^{:doc "Runs code linter"}
                                  ["clj-kondo" "--lint" "src"]

                                  "bundle-size"  ^{:doc "creates a js bundle report"}
                                  ["with-profile" "+cljs" "run" "-m" "dev.bundle-size"]

                                  "outdated" ^{:doc "Runs ancient"}
                                  ["with-profile" "+cljs" "ancient"]

                                  "tree" ^{:doc "Runs deps tree with correct profile"}
                                  ["with-profile" "+cljs" "deps" ":tree"]

                                  "bump-version"
                                  ["change" "version" "leiningen.release/bump-version"]

                                  "shadow-compile"  ^{:doc "compiles UI"}
                                 ;["shell" "shadow-cljs" "compile" "web"]
                                  ["with-profile" "+cljs" "run" "-m" "shadow.cljs.devtools.cli" "compile" ":web"]}

                   :cloverage    {:codecov? true
                                  ;; In case we want to exclude stuff
                                  ;; :ns-exclude-regex [#".*util.instrument"]
                                  ;; :test-ns-regex [#"^((?!debug-integration-test).)*$$"]
                                  }
                   ;; TODO : Make cljfmt really nice : https://devhub.io/repos/bbatsov-cljfmt
                   :cljfmt       {:indents {as->                [[:inner 0]]
                                            with-debug-bindings [[:inner 0]]
                                            merge-meta          [[:inner 0]]
                                            try-if-let          [[:block 1]]}}}

             :demo {:source-paths ["profiles/demo/src"]
                    :dependencies [[org.clojure/tools.logging "1.1.0"] ; needed by clojisr
                                   [org.pinkgorilla/clojisr-gorilla "0.0.6"]]}}


  :aliases {"goldly" ^{:doc "Runs goldly app (with only default system components)"}
            ["run" "-m" "goldly.app"]

            "demo" ^{:doc "Runs goldly app (with demo components)"}
            ["with-profile" "+demo" "run" "-m" "goldly.app" "./profiles/demo/src/systems/"]})

