(ns goldly.app
  (:require
   [cljs.pprint]
   [taoensso.timbre :as timbre :refer-macros (tracef debugf infof warnf errorf info)]
   [reagent.dom]
   [re-frame.core :refer [clear-subscription-cache! subscribe]]

   ; the following just adds dependencies to bundle
   [pinkgorilla.ui.default-setup] ; renderable-ui
   [pinkgorilla.ui.default-renderer] ; gorilla-ui 
   [pinkgorilla.ui.gorilla-plot.pinkie] ; gorilla-plot
   ;[pinkgorilla.ui.pinkie :refer [tag-inject renderer-list]]
   [pinkgorilla.ui.config :refer [set-prefix!]]

   [goldly.web.ws :refer [start-router!]]
   [goldly.web.views :refer [goldly-app-page]]
   [goldly.web.routes-old :refer [app-routes]]
   [goldly.web.routes :refer [init-routes]]

   [goldly.events] ; add reframe event handlers
   [goldly.puppet.subs]
   [goldly.puppet.db]))

(set-prefix! "/r/")

(defn print-log-init! []
  (enable-console-print!)
;(timbre/set-level! :trace) ; Uncomment for more logging
  (timbre/set-level! :debug)
  #_(timbre/set-level! :info))

(defn mount-app []
  (reagent.dom/render [goldly-app-page]
                      (.getElementById js/document "app")))

 ; (secretary/dispatch! route))
; 

;; before-reload is a good place to stop application stuff before we reload.
(defn ^:dev/before-load before-reload []
  (println "shadow-cljs reload: before")
  (info "shadow-cljs reload: before"))

(defn ^:dev/after-load after-reload []
  (print-log-init!)
  (println "shadow-cljs reload: after")
  (info "shadow-cljs reload: after")

  (println "clearing reframe subscription cache..")
  (clear-subscription-cache!)

  (println "re-loading configuration from server..")
  ;(dispatch [:load-config])

  (init-routes)
  (start-router!)
  (println "mounting notebook-app ..")
  (mount-app))

(after-reload)