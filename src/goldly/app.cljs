(ns goldly.app
  (:require
   [taoensso.timbre :as timbre :refer-macros [tracef debugf infof warnf errorf info]]
   [webly.web.app]
   [webly.config :refer [webly-config]]
   [goldly.web.ws :refer [start-router!]]
   [goldly.web.views] ; side-effects
   [goldly.puppet.loader] ; side-effects
   [goldly.events] ; side-effects
   [goldly.puppet.subs] ; side-effects
   [goldly.puppet.db] ; side-effects
   [goldly.web.routes :refer [goldly-routes-backend]]
   ; pinkie is a necessary dependency, because goldly systems use it for frontend description   
   [pinkie.default-setup] ; side-effecs
   ))

(defn ^:export start []
  (swap! webly-config assoc :timbre-loglevel :info)
  (info "goldly starting ..")
  (webly.web.app/start goldly-routes-backend)
  (webly.web.app/mount-app)
  (start-router!))

;(start)