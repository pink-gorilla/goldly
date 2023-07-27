(ns goldly.service.expose
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [goldly.service.core]))

(defn resolve-symbol [s]
  (try
    (info "resolving: " s)
    [s (requiring-resolve s)]
    (catch Exception ex
      (error "Exception in resolving: " s)
      (throw ex))))

(defn start-services [{:keys [symbols permission] :as args}]
  (info "start-services: " symbols " permission: " permission)
  (let [services (->> (map resolve-symbol symbols)
                      (into {}))]
    (goldly.service.core/add services)
    args ; to stop this services.
    ))
(defn stop-services [{:keys [symbols permission]}]
  (warn "stop-services: " symbols " permission: " permission " - not implemented"))

; todo: 1. permissions 2. stop service

