(ns goldly.app
  "the main goldly application"
  (:require
   [clojure.java.io]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [com.rpl.specter :refer [transform setval END ALL]]
   [webly.config :refer [get-in-config config-atom]]
   [webly.profile :refer [compile? server?]]
   [webly.config :refer [load-config! add-config]]
   [webly.user.app.app :refer [webly-run!]]

   ; compile time
   [goldly.version :refer [print-version]]
   [goldly.extension.discover :as d]
   [goldly.extension.pinkie :refer [available]]
   [goldly.sci.bindings :refer [generate-bindings]]

   ; runtime
   [goldly.routes] ; side effects
   [goldly.cljs.loader :refer [cljs-watch]]
   [goldly.ws-connect :refer [start-ws-conn-watch]]
   [goldly.service.core]
   [goldly.service.handler]
   [goldly.services]
   [goldly.require-clj :refer [require-clj-namespaces]]
   [goldly.nrepl-server :refer [run-nrepl-server]])
  (:gen-class))

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
      (warn "no autoload-clj-ns defined!")
      (do (info "loading clj namespaces: " autoload-clj-ns)
          (require-clj-namespaces autoload-clj-ns)))

    (cljs-watch)
    (start-ws-conn-watch)
    ;(if extensions
    ;  (do (info "loading extensions from ns: " extensions)
    ;      (require-namespaces extensions))
    ;  (warn "no goldly extensions defined!"))
    (run-nrepl-server (get-in-config [:nrepl]))))

(defn goldly-server-run!
  [{:keys [config profile] ; a map so it can be consumed by tools deps -X
    :or {profile "jetty"
         config {}}}]
  (let [config (add-config "goldly.edn" config)]
    (load-config! config)
    (goldly-init!)
    (when (compile? profile)
      (goldly-compile!))
    (when (server? profile)
      (goldly-run!))
    (webly-run! profile config)))

(defn -main ; for lein alias
  ([]
   (goldly-server-run! {}))
  ([config]
   (goldly-server-run! {:config config}))
  ([config profile]   ; when config and profile are passed, config first (because profile then can get changed in cli)
   (goldly-server-run! {:profile profile
                        :config config})))