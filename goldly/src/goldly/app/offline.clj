(ns goldly.app.offline
  (:require
   [taoensso.timbre :refer [debug info infof warn error]]
   [modular.config :refer [get-in-config load-config!]]
   [goldly.offline.build :refer [goldly-build-static]]))

(defn goldly-static [{:keys [config page-symbol sci-cljs-dirs]}]
  (require '[modular.config])
  (warn "loading config: " config)
  (load-config! config)
  (let [goldly-config (get-in-config [:goldly])]
    (goldly-build-static goldly-config page-symbol sci-cljs-dirs)))
