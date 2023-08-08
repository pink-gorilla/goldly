(ns goldly.app.static
  (:require
   [taoensso.timbre :refer [debug info infof warn error]]
   [modular.config :refer [get-in-config load-config!]]
   [goldly.static.build :refer [goldly-build-static]]))

(defn goldly-static [{:keys [config page-symbol]}]
  (require '[modular.config])
  (warn "loading config: " config)
  (load-config! config)
  (let [goldly-config (get-in-config [:goldly])]
    (goldly-build-static goldly-config page-symbol)))
