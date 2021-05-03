(ns goldly-server.app
  (:require
   [webly.user.app.app :refer [webly-run!]]
   [webly.profile :refer [setup-profile server?]]
   ; side-effects
   [goldly.routes]
   [goldly.app :refer [goldly-run!]]
   [goldly-server.routes])
  (:gen-class))

(defn -main
  [profile-name]
  (let [profile (setup-profile profile-name)]
    (when (:server profile)
      (goldly-run!))
    (webly-run! profile-name)))

