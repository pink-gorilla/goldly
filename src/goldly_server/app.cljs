(ns goldly-server.app
  (:require
   [webly.user.app.app :refer [webly-run!]]
   ;[goldly.routes :refer [routes-api routes-app]]
   ; side-effects
   [goldly.app]
   [goldly-server.routes :refer [routes-api routes-app]]
   [goldly-server.events]
   [goldly-server.pages.about]))

(defn ^:export start []
  (webly-run! routes-api routes-app))

