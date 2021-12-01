(ns goldly.app
  "the main goldly application"
  (:require
   [clojure.java.io]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [com.rpl.specter :refer [transform setval END ALL]]
   [modular.config :refer [get-in-config config-atom load-config! add-config require-namespaces]]
   ; webly build-tool
   [webly.build.profile :refer [compile? server?]]
   [webly.app.app :refer [webly-run!]]

   ; compile time
   [goldly.version :refer [print-version]]
   [goldly.extension.discover :refer [discover-extensions]]
   [goldly.extension.pinkie :refer [available]]
   [goldly.extension.cljs-autoload]
   [goldly.sci.bindings :refer [generate-bindings]]
   [goldly.cljs.discover :refer [cljs-watch generate-cljs-autoload]]
   [goldly.static :refer [export-sci-cljs]]

   ; runtime
   [goldly.routes] ; side effects
   [goldly.ws-connect :refer [start-ws-conn-watch]]
   [goldly.service.core]
   [goldly.service.handler]
   [goldly.services]
   [goldly.nrepl-server :refer [run-nrepl-server]])
  (:gen-class))

(defn goldly-init! []
    ; extensions can add to cljs namespaces. therefore extensions have to
    ; be included at compile time. But extensions also are needed
    ; for css and clj ns. Therefore put to init
  (print-version "goldly")
  (discover-extensions)
  (info "pinkie renderer (clj): " (available)))

(defn goldly-compile! []
  (let [{:keys [systems]}
        (get-in-config [:goldly])]
    ;(if extensions
    ;  (do (info "loading extensions from ns: " extensions)
    ;      (require-namespaces extensions))
    ;  (warn "no goldly extensions defined!"))
    (generate-bindings)
    (generate-cljs-autoload)
    (export-sci-cljs)))

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
  (let [{:keys [autoload-clj-ns routes]
         :or {routes {}}}
        (get-in-config [:goldly])]

    ; add goldly user-app routes
    (if (empty? routes)
      (do
        (warn "no [:goldly :routes ] defined - you will see a blank page.")
        (add-routes {:app {"" :goldly/no-page}
                     :api {}}))
      (add-routes routes))

    (if (empty? autoload-clj-ns)
      (warn "no user autoload-clj-ns defined!")
      (do (info "loading user clj namespaces: " autoload-clj-ns)
          (require-namespaces autoload-clj-ns)))

    (cljs-watch)
    (start-ws-conn-watch)
    ;(if extensions
    ;  (do (info "loading extensions from ns: " extensions)
    ;      (require-namespaces extensions))
    ;  (warn "no goldly extensions defined!"))
    (run-nrepl-server (get-in-config [:nrepl]))))

(defonce goldly-default-edn (atom "goldly.edn"))

(defn goldly-server-run!
  [{:keys [config profile] ; a map so it can be consumed by tools deps -X
    :or {profile "jetty"
         config {}}}]
  (let [config (add-config @goldly-default-edn config)]
    (load-config! config)
    (goldly-init!)
    (when (compile? profile)
      (goldly-compile!))
    (when (server? profile)
      (goldly-run!))
    (webly-run! profile config)))

