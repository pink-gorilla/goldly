(ns goldly.app
  "the main goldly application"
  (:require
   [clojure.java.io]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [com.rpl.specter :refer [transform setval END ALL]]
   [webly.config :refer [get-in-config config-atom]]
   [webly.writer :refer [write-status]]
   [goldly.notebook.picasso] ; side-efects
   [goldly.system.require :refer [require-namespaces]]
   [goldly.sci.bindings :refer [generate-bindings]]
   [goldly.ws]
   [goldly.api.handler]
   [goldly.extension.discover :as d]
   [goldly.service.core]
   [goldly.broadcast.core]
   [goldly.store.file]
   [goldly.store.watch :refer [cljs-watch]]
   [goldly.version :refer [print-version]]))

(defn goldly-compile! []
  (let [{:keys [systems]}
        (get-in-config [:goldly])]
    ; extensions can add to cljs namespaces. therefore extensions have to
    ; be included at compile time.
    (d/discover)
    ;(if extensions
    ;  (do (info "loading extensions from ns: " extensions)
    ;      (require-namespaces extensions))
    ;  (warn "no goldly extensions defined!"))
    (generate-bindings)))

(defn add-user-routes [routes]
  (let [m (fn [r]
            (debug "merging.." r)
            (merge r routes))]
    (debug "adding goldly user-app routes: " routes)
    ;(write-status "goldly-routes1" @config-atom)
    (reset! config-atom (transform [:webly :routes :app] m @config-atom))
    ;(write-status "goldly-routes2" @config-atom)
    ))

(defn goldly-run! []
  (let [{:keys [systems routes]
         :or {routes {}}}
        (get-in-config [:goldly])]

    (print-version "goldly")

    ; add goldly user-app routes
    (if (empty? routes)
      (warn "no goldly user routes defined - you will not see custom pages.")
      (add-user-routes routes))

    ; systems are stored in clj files
    (if systems
      (do (info "loading systems from ns: " systems)
          (require-namespaces systems))
      (warn "no goldly systems defined!"))

    (cljs-watch)
    ;(if extensions
    ;  (do (info "loading extensions from ns: " extensions)
    ;      (require-namespaces extensions))
    ;  (warn "no goldly extensions defined!"))
    ))


