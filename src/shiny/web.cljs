(ns shiny.web
  (:require
   [cljs.pprint]
   [reagent.core :as r]
   [reagent.dom]
   [pinkgorilla.ui.pinkie :refer [tag-inject renderer-list]]
   ; add dependencies of this project to bundle
   [pinkgorilla.ui.default-renderer]
   [shiny.core]))


(defn routes []
  {:status "running system stats"
   :systems "id or route-name parameter displays system"})


(defn app []
  [:h1 "shiny"]
  )


(defn stop []
  (js/console.log "Stopping..."))

(defn start []
  (js/console.log "Starting...")
  ;(js/console.log (print-registered-tags))
  (reagent.dom/render (tag-inject app)
                      (.getElementById js/document "app")))

(defn ^:export init []
  (start))