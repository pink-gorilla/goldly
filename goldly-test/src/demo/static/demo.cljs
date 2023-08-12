(ns demo.static.demo
  (:require
   [ui.bidi]))

(def routes
  {"" :user/main
   "lazy" :user/lazy
   "error" :user/error
   "select" :user/select
   "tick" :user/tick

    ;devtools
   "devtools/help" :devtools
   "devtools/repl" :repl
   "devtools/pages" :pages
   "devtools/viewer" :viewer
   "devtools/build" :build
   "devtools/runtime" :runtime
   "devtools/theme" :theme})

(defn init []
  (println "initializing bidi router...")
  (ui.bidi/start-router! routes)
  (ui.bidi/goto! :user/main))

(defn router-page []
  [ui.bidi/page-viewer])

