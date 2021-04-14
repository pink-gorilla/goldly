(ns demo.app
  (:require
   [taoensso.timbre :as timbre :refer-macros [info]]
   [webly.user.app.app :refer [webly-run!]]
   ;[goldly.routes :refer [routes-api routes-app]]
   ; side-effects
   [goldly.app] 
   [demo.views]
   [demo.routes :refer [routes-api routes-app]]
   [demo.events]
   ))

(defn ^:export start []
  (webly-run! routes-api routes-app))

