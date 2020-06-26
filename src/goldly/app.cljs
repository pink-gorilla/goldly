(ns goldly.app
  (:require
   [cljs.pprint]
   [taoensso.timbre :as timbre :refer-macros (tracef debugf infof warnf errorf info)]
   [reagent.dom]
   [re-frame.core :refer [clear-subscription-cache! subscribe]]
   [pinkie.default-setup] ; side-effecs pinkie
   [picasso.default-config] ; side-efects picasso
   [pinkgorilla.ui.default-renderer] ; side-effects gorilla-ui 
   ;[pinkgorilla.ui.gorilla-plot.pinkie] ; side-effects gorilla-plot TODO: update to pinkie v2
   [pinkgorilla.ui.config :refer [set-prefix!]]
   [goldly.web.ws :refer [start-router!]]
   [goldly.web.views :refer [goldly-app-page]]
   [goldly.web.routes :refer [init-routes]]
   [goldly.events] ; add reframe event handlers
   [goldly.puppet.subs]
   [goldly.puppet.db]
   [goldly.user.auth.view] ; side effects
   ))

(set-prefix! "/r/")

(defn print-log-init! []
  (enable-console-print!)
;(timbre/set-level! :trace) ; Uncomment for more logging
  (timbre/set-level! :debug)
  #_(timbre/set-level! :info))

(defn mount-app []
  (reagent.dom/render [goldly-app-page]
                      (.getElementById js/document "app")))

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