(ns goldly.app.run
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [com.rpl.specter :refer [transform setval END ALL]]
   [modular.config :refer [get-in-config config-atom load-config! add-config resolve-config-key]]
   [webly.build.profile :refer [server? #_compile?]]
   [goldly.extension.discover :refer [discover-extensions]]
   [goldly.cljs.discover :refer [cljs-watch]]
   [goldly.ws-connect :refer [start-ws-conn-watch]]
   ; side effects  (needed at runtime)
   [goldly.service.core]
   [goldly.service.handler]
   [goldly.services]))

(defn add-routes [routes]
  (let [m (fn [r]
            (let [{:keys [app api]
                   :or {app {}
                        api {}}} (or routes {})]
              (debug "merging.." r)
              {:app (merge (:app r) app)
               :api (merge (:api r) api)}))]
    (debug "adding goldly user routes: " routes)
    ;(write-status "goldly-routes1" @config-atom)
    (reset! config-atom (transform [:webly :routes] m @config-atom))
    ;(write-status "goldly-routes2" @config-atom)
    ))

(defn start-goldly-services []
  (let [{:keys [routes]
         :or {routes {}}}
        (get-in-config [:goldly])
        routes (if (empty? routes)
                 (do (warn "no [:goldly :routes ] defined - you will see a blank page.")
                     {:app {"" :goldly/no-page}
                      :api {}})
                 routes)]
    ; add goldly user-app routes
    (add-routes routes)
    (let [w-cljs (cljs-watch)
          w-conn (start-ws-conn-watch)]
      {:cljs-watch w-cljs
       :ws-watch w-conn
       :ns-clj (get-in-config [:ns-clj])
       :webly (get-in-config [:webly])})))

(defn start-goldly [config profile] ; profile "jetty"
  (warn "resolving [:webly :routes]")
  (resolve-config-key config [:webly :routes])
  (warn "discovering extensions..")
  (discover-extensions)
  (when (server? profile)
    (info "starting goldly services (cljs-watch, ws-watch)..")
    (start-goldly-services)))

(defn stop-goldly [{:keys [cljs-watch ws-watch]}]
  (info "stopping webly..")
  (when cljs-watch
    (warn "stopping goldly cljs-watch (not implemented)"))
  (when ws-watch
    (warn "stopping goldly ws-watch (not implemented)")))

;(defonce goldly-default-config (atom ["webly/config.edn" "goldly.edn"]))
;(add-config @goldly-default-config config)
