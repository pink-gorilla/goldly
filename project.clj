(defproject org.pinkgorilla/goldly "0.2.7-SNAPSHOT"
  :description "reactive html ui with clj-cljs interop"
  :license {:name "MIT"}
  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/release_username
                                     :password :env/release_password
                                     :sign-releases false}]]
  :min-lein-version "2.9.3"
  :min-java-version "1.11"

  :prep-tasks [; "compile"
               ;"resource"
               ]

  :release-tasks [["vcs" "assert-committed"]
                  ["bump-version" "release"]
                  ["vcs" "commit" "Release %s"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["deploy"]
                  ["bump-version"]
                  ["vcs" "commit" "Begin %s"]
                  ["vcs" "push"]]

  :managed-dependencies [[org.clojure/core.async "1.3.610"]
                         [org.clojure/tools.logging "1.1.0"]
                         [org.clojure/core.memoize "1.0.236"]
                         ; libpythonclj fixes
                         [net.java.dev.jna/jna "5.6.0"]
                         [org.ow2.asm/asm "8.0.1"]
                         [nrepl "0.8.0-alpha1"]]

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/core.async]
                 [org.pinkgorilla/webly "0.0.22"] ; brings shadow
                 [http-kit "2.3.0"] ; sente needs httpkit
                 
                 ; goldly.user.config:
                 ;[cprop "0.1.14"] ; configuration
                 ;[mount "0.1.16"]
                 ;[expound "0.7.2"] ; see clojurewb

                 [com.taoensso/timbre "4.10.0"]  ; clojurescript logging
                 [com.taoensso/encore "2.122.0"]
                 [com.taoensso/sente "1.15.0"
                  :exclusions [aleph
                               org.clojure/core.async
                               org.immutant
                               info.sunng/ring-jetty9-adapter]] ;  websocket
                 [org.clojure/data.json "1.0.0"]
                 [com.rpl/specter "1.1.3"]
                 ;[org.clojure/java.classpath "1.0.0"]
                 [org.pinkgorilla/pinkie "0.2.10"]
                 [org.pinkgorilla/picasso "3.1.18"]
                 [org.pinkgorilla/nrepl-middleware "0.3.11"]
                 [org.pinkgorilla/gorilla-ui "0.2.28"
                  :exclusions [org.clojure/clojurescript]]
                 [org.pinkgorilla/notebook-ui "0.0.70"]
                 [org.pinkgorilla/gorilla-plot "1.2.2"
                  :exclusions [org.clojure/clojurescript]]
                 ]

  :source-paths ["src"]

  :resource-paths ["resources" ; resources that goldly brings (not in maven/npm)
                   "target/webly" ; js bundle
                   #_"target/node_modules"] ; css png resources from npm modules


  :target-path  "target/jar"
  :clean-targets ^{:protect false} [:target-path
                                    [:goldly :builds :app :compiler :output-dir]
                                    [:goldly :builds :app :compiler :output-to]]

  :profiles {:cljs {:dependencies [;[org.clojure/clojurescript "1.10.773"] ; webly
                                   [thi.ng/strf "0.2.2"]
                                   [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]
                                   ; compiler 
                                   [funcool/promesa "4.0.2"]
                                   [paprika "0.1.3"] ; mauricio helper functions 
                                   [borkdude/sci "0.0.13-alpha.17"]
                                   [rewrite-cljs "0.4.4"]
                                   [org.rksm/suitable "0.3.2"  :exclusions [org.clojure/clojurescript]]
                                   [cider/orchard "0.5.8"]
                                   [etaoin "0.3.6"]]}

             :dev {:source-paths ["profiles/demo/src"
                                  "test"]
                   :dependencies [[clj-kondo "2020.06.21"]
                                  [ring/ring-mock "0.4.0"]]
                   :plugins      [[lein-cljfmt "0.6.6"]
                                  [lein-cloverage "1.1.2"]
                                  [lein-codox "0.10.7"]
                                  [lein-shell "0.5.0"]
                                  [lein-ancient "0.6.15"]
                                  [min-java-version "0.1.0"]
                                  [lein-resource "17.06.1"]
                                  ;[lein-environ "1.1.0"] ;; TODO Will likely be axed soon
                                  ]
                   :aliases      {"lint" ^{:doc "Runs code linter"}
                                  ["run" "-m" "clj-kondo.main" "--lint" "src"]

                                  "outdated" ^{:doc "Runs ancient"}
                                  ["with-profile" "+cljs" "ancient"]

                                  "tree" ^{:doc "Runs deps tree with correct profile"}
                                  ["with-profile" "+cljs" "deps" ":tree"]

                                  "bump-version"
                                  ["change" "version" "leiningen.release/bump-version"]}

                   :aot []
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
                    :resource-paths ["profiles/demo/resources"]
                    :dependencies []}}


  :aliases {; shadow-compile is only needed to update package.json with transitive dependencies
            "shadow-compile"
            ["with-profile" "+cljs" "run" "-m" "shadow.cljs.devtools.cli" "compile" ":webly"]

             ;; APP

            "build-dev"  ^{:doc "compiles bundle-dev"}
            ["with-profile" "+demo" "run" "-m" "webly.build-cli" "compile" "+cljs" "goldly.app/handler" "goldly.app"]

            "build-prod"  ^{:doc "compiles bundle-prod"}
            ["with-profile" "+demo" "run" "-m" "webly.build-cli" "release" "+cljs" "goldly.app/handler" "goldly.app"]

            "goldly-tidy"  ^{:doc "runs compiled bundle on shadow dev server"}
            ["with-profile" "+demo" "run" "-m" "goldly.app"]

            "goldly"  ^{:doc "runs compiled bundle on shadow dev server"}
            ["with-profile" "+demo" "run" "-m" "goldly.app"  "profiles/demo/src/systems/"]})

