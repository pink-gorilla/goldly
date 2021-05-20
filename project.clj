(defproject org.pinkgorilla/goldly "0.2.50"
  :description "reactive html ui with clj-cljs interop"
  :license {:name "MIT"}
  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/release_username
                                     :password :env/release_password
                                     :sign-releases false}]]
  :min-lein-version "2.9.3"
  ;:min-java-version "1.11"

  :release-tasks [["vcs" "assert-committed"]
                  ["bump-version" "release"]
                  ["vcs" "commit" "Release %s"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["deploy"]
                  ["bump-version"]
                  ["vcs" "commit" "Begin %s"]
                  ["vcs" "push"]]


  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/core.async "1.3.618"]
                 [org.clojure/data.json "2.1.0"]
                 [com.rpl/specter "1.1.3"]
                 [thi.ng/strf "0.2.2"
                  :exclusions [org.clojure/clojurescript]]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]
                 [borkdude/sci "0.2.5"] ; sci compiler  
                 [fipp "0.6.23"] ; edn pretty printing
                 [org.pinkgorilla/pinkie "0.3.3"] ; frontend ui layout
                 [org.pinkgorilla/picasso "3.1.21"] ; type rendering
                 [org.pinkgorilla/webly "0.2.39"]
                 [org.pinkgorilla/ui-codemirror "0.0.4"]
                 [cljs-ajax "0.8.3"]]

  :source-paths ["src"]
  :resource-paths ["resources" ; notebooks
                   "target/webly"] ; js bundle
  :target-path  "target/jar"
  :clean-targets ^{:protect false} [:target-path
                                    [:goldly :builds :app :compiler :output-dir]
                                    [:goldly :builds :app :compiler :output-to]]

  :profiles {:bundel {:dependencies [; bundled dependencies
                                     [org.pinkgorilla/gorilla-ui "0.3.23" :exclusions [org.clojure/clojurescript]]
                                     [org.pinkgorilla/gorilla-plot "1.2.9" :exclusions [org.clojure/clojurescript]]
                                     [org.pinkgorilla/ui-binaryclock "0.0.4"]
                                     [org.pinkgorilla/ui-quil "0.1.5"]]}

             :dev {:source-paths ["test"]
                   :dependencies [[clj-kondo "2021.04.23"]
                                  ;[ring/ring-mock "0.4.0"]
                                  ]
                   :plugins      [[lein-cljfmt "0.6.6"]
                                  [lein-cloverage "1.1.2"]
                                  [lein-codox "0.10.7"]
                                  [lein-shell "0.5.0"]
                                  [lein-ancient "0.6.15"]
                                  ;[min-java-version "0.1.0"]
                                  ;[lein-resource "17.06.1"]
                                  ]
                   :aliases      {"lint" ^{:doc "Runs code linter"}
                                  ["run" "-m" "clj-kondo.main" "--lint" "src"]

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
                                            try-if-let          [[:block 1]]}}}}

  :aliases {"goldly"  ^{:doc "runs compiled bundle on shadow dev server"}
            ["run" "-m" "goldly-server.app" "watch"]
            
            "goldly-bundel" 
            ["with-profile" "+bundel"
             "run" "-m" "goldly-server.app" "watch" "goldly-bundel.edn"]
            
            })

