(ns goldly-server.app
  (:require
   [taoensso.timbre :as timbre :refer [info warn]]
   [webly.config :refer [load-config! add-config]]
   [webly.user.app.app :refer [webly-run!]]
   [webly.profile :refer [compile? server?]]
   [goldly.app :refer [goldly-compile! goldly-run!]]
   ; side-effects
   [goldly-server.routes])
  (:gen-class))

(defn goldly-server-run!
  [{:keys [config profile] ; a map so it can be consumed by tools deps -X
    :or {profile "jetty"
         config {}}}]
  (let [config (add-config "goldly.edn" config)]
    (load-config! config)
    (when (compile? profile)
      (goldly-compile!))
    (when (server? profile)
      (goldly-run!))
    (webly-run! profile config)))

(defn -main ; for lein alias
  ([]
   (goldly-server-run! {}))
  ([profile]
   (goldly-server-run! {:profile profile}))
  ([config profile]   ; when config and profile are passed, config first (because profile then can get changed in cli)
   (goldly-server-run! {:profile profile
                        :config config})))
