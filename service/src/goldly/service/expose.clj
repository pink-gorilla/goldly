(ns goldly.service.expose
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [modular.permission.service :refer [add-permissioned-services]]
   [goldly.service.core]))

(defn resolve-symbol [s]
  (try
    (debug "resolving: " s)
    [s (requiring-resolve s)]
    (catch Exception ex
      (error "Exception in resolving: " s)
      (throw ex))))


(defn set-permission [symbols permission]
  (let [data (->> (map (fn [s] [s permission]) symbols)
                  (into {}))]
    ; {'time/now-date #{} 
    ;  'time/local nil}
   
    (add-permissioned-services data)))

(comment 
  (set-permission ['crb.report/profit-loss 'crb.report/overdues] #{:management})
;
)

(defn start-services [{:keys [symbols permission name]
                       :as args
                       :or {name "services"}
                       }]
  (info "exposing [" name "]   permission: " permission " symbols: " symbols)
  (let [services (->> (map resolve-symbol symbols)
                      (into {}))]
    (goldly.service.core/add services)
    (set-permission symbols permission)
    args ; to stop this services.
    ))

(defn stop-services [{:keys [symbols permission]}]
  (warn "stop-services: " symbols " permission: " permission " - not implemented"))

; todo: 1. permissions 2. stop service

