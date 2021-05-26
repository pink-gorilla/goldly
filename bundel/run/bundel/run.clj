(ns bundel.run
  (:require
   [goldly-server.app])
  (:gen-class))


(defn -main ; for lein alias
  []
  (goldly-server.app/goldly-server-run!
   {:profile "jetty"
    :config  "goldly-bundel.edn"}))