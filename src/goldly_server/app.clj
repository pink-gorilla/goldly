(ns goldly-server.app
  (:require
   [taoensso.timbre :as timbre :refer [info warn]]
   [webly.config :refer [load-config! add-config]]
   [webly.user.app.app :refer [webly-run!]]
   [webly.profile :refer [server?]]
   [goldly.app :refer [goldly-run!]]
   ; side-effects
   [goldly-server.routes])
  (:gen-class))

(defn goldly-server-run!
  [{:keys [config profile] ; a map so it can be consumed by tools deps -X
    :or {profile "jetty"
         config {}}}]
  (let [config (add-config "goldly.edn" config)]
    (if (server? profile)
      (do (load-config! config)
          (goldly-run!))
      (warn "no server mode. not running goldly"))
    (webly-run! profile config)))

(defn -main ; for lein alias
  ([]
   (goldly-server-run! {}))
  ([profile]
   (goldly-server-run! {:profile profile})))
