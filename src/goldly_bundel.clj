(ns goldly-bundel
  (:require
   [goldly-server.app])
  (:gen-class))

(defn run [{:keys [profile config]
            :or {profile "jetty"
                 config "goldly-bundel.edn"}}]
  (goldly-server.app/goldly-server-run!
   {:profile profile
    :config config}))

(defn -main ; for lein alias
  ([]
   (run {}))
  ([config]
   (run {:config config})))