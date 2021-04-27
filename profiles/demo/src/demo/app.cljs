(ns demo.app
  (:require
   [webly.user.app.app :refer [webly-run!]]
   ;[goldly.routes :refer [routes-api routes-app]]
   ; side-effects
   [goldly.app]
   [demo.routes :refer [routes-api routes-app]]
   [demo.events]
   ))

(defn ^:export start []
  (webly-run! routes-api routes-app))

