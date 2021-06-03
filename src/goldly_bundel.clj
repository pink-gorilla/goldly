(ns goldly-bundel
  (:require
   [webly.config :refer [add-config]]
   [goldly-server.app])
  (:gen-class))

(defn run [{:keys [profile config]
            :or {profile "jetty"
                 config {}}}]
  (let [config (add-config "goldly-bundel.edn" config)]
    (goldly-server.app/goldly-server-run!
     {:profile profile
      :config config})))

(defn -main ; for lein alias
  ([]
   (run {}))
  ([config]
   (run {:config config})))