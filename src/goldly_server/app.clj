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
  [user-config profile-name]
  (let [config ["goldly.edn" user-config]
        profile (setup-profile profile-name config)]
    (when (:server profile)
      (goldly-run!))
    (webly-run! profile-name config)))

(defn -main
  [user-config profile-name]
  (goldly-server-run! user-config profile-name))
