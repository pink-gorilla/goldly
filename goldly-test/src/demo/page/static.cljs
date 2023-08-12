(ns demo.page.static
  (:require
   [ui.bidi]))

(println "initializing bidi router...")
(ui.bidi/init!
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

(ui.bidi/goto! :user/main)

(defn router-page [{:keys [_handler _route-params _query-params] :as _route}]
  [ui.bidi/page-viewer])

