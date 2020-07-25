(ns goldly.app
  (:require
   [taoensso.timbre :as timbre :refer-macros (tracef debugf infof warnf errorf info)]
   [pinkie.default-setup] ; side-effecs pinkie
   [picasso.default-config] ; side-efects picasso
   [pinkgorilla.ui.default-renderer] ; side-effects gorilla-ui 
   ;[pinkgorilla.ui.gorilla-plot.pinkie] ; side-effects gorilla-plot TODO: update to pinkie v2
   [webly.web.app]
   [webly.config :refer [webly-config]]
   [goldly.web.ws :refer [start-router!]]
   [goldly.web.views] ; side-effects
   [goldly.web.routes :refer [goldly-routes-backend]]
   [goldly.events] ; side-effects
   [goldly.puppet.subs] ; side-effects
   [goldly.puppet.db]))


(defn ^:export start []
  (swap! webly-config assoc :timbre-loglevel :info)
  (info "goldly starting ..")
  (start-router!)
  (webly.web.app/start goldly-routes-backend)
  (webly.web.app/mount-app))