(ns goldly-server.app
  (:require
   [webly.config :refer [load-config!]]
   [webly.user.app.app :refer [webly-run!]]
   [webly.profile :refer [setup-profile server?]]
   ; side-effects
   [goldly.routes]
   [goldly.app :refer [goldly-run!]]
   [goldly-server.routes])
  (:gen-class))

(defn goldly-server-run!
  [profile-name user-config]
  (load-config! (or user-config {}))
  (let [profile (setup-profile profile-name)]
    (when (:server profile)
      (goldly-run!))
    (webly-run! profile-name)))

(defn -main
  [profile-name]
  (goldly-server-run! profile-name {}))
