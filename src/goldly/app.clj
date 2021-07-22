(ns goldly.app
  "the main goldly application"
  (:require
   [clojure.java.io]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [com.rpl.specter :refer [transform setval END ALL]]
   [webly.config :refer [get-in-config config-atom]]

   [goldly.version :refer [print-version]]
   [goldly.extension.discover :as d]
   [goldly.extension.pinkie :refer [available]]

   [goldly.service.core]
   [goldly.service.handler]
   [goldly.services]

   [goldly.broadcast.core]
   [goldly.cljs.loader :refer [cljs-watch]]
   [goldly.component.type.notebook :refer [notebook-watch]]
   [goldly.sci.bindings :refer [generate-bindings]]
   [goldly.component.type.system]
   [goldly.component.ws-connect :refer [start-ws-conn-watch]]

   [goldly.system.require :refer [require-namespaces]]
   [goldly.notebook.picasso]

   [goldly.scratchpad.handler]
   [goldly.scratchpad.core]

   [pinkgorilla.nrepl.service]))

(defn goldly-init! []
    ; extensions can add to cljs namespaces. therefore extensions have to
    ; be included at compile time. But extensions also are needed
    ; for css and clj ns. Therefore put to init
  (print-version "goldly")
  (d/discover)
  (info "pinkie renderer (clj): " (available)))

(defn goldly-compile! []
  (let [{:keys [systems]}
        (get-in-config [:goldly])]
    ;(if extensions
    ;  (do (info "loading extensions from ns: " extensions)
    ;      (require-namespaces extensions))
    ;  (warn "no goldly extensions defined!"))
    (generate-bindings)))

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

(defn goldly-run! []
  (let [{:keys [systems routes]
         :or {routes {}}}
        (get-in-config [:goldly])]

    ; add goldly user-app routes
    (if (empty? routes)
      (warn "no goldly user routes defined - you will not see custom pages.")
      (add-routes routes))

    ; systems are stored in clj files
    (if systems
      (do (info "loading systems from ns: " systems)
          (require-namespaces systems))
      (warn "no goldly systems defined!"))

    (cljs-watch)
    (notebook-watch)
    (start-ws-conn-watch)
    ;(if extensions
    ;  (do (info "loading extensions from ns: " extensions)
    ;      (require-namespaces extensions))
    ;  (warn "no goldly extensions defined!"))
    (pinkgorilla.nrepl.service/start-nrepl (get-in-config [:nrepl]))))


