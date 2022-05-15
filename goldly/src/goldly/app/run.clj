(ns goldly.app.run
  (:require
   [clojure.string] ; side effects
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [com.rpl.specter :refer [transform setval END ALL]]
   [modular.config :refer [get-in-config config-atom load-config! add-config resolve-config-key]]
   [modular.writer :refer [write-status write-target]]
   [modular.require :refer [require-namespaces]]
   [webly.build.profile :refer [server? #_compile?]]
   [goldly.config.runtime :refer [runtime-config]]
   [goldly.run.cljs-load :refer [add-cljs-file-services start-cljs-watch]]
   [goldly.run.ws-connect :refer [start-ws-conn-watch]]
   [goldly.run.services] ; side effects..
   [goldly.routes] ; side effects
   ))

(defn write-extensions-runtime [ext-map]
  (->> ext-map
       vals
       (map #(dissoc % :pinkie ; depreciated
                     :cljs-namespace  ; build-config
                     :cljs-bindings
                     :cljs-ns-bindings))
       (sort-by :name)
       (write-target "goldly-run-ext")))

(defn set-webly-routes []
  (warn "resolving [:webly :routes]")
  (resolve-config-key config-atom [:webly :routes])
  (let [{:keys [routes] :or {routes {}}} (get-in-config [:goldly])
        routes (if (empty? routes)
                 (do (warn "no [:goldly :routes ] defined - you will see a blank page.")
                     {:app {"" :goldly/no-page}
                      :api {}})
                 routes)
        m (fn [r]
            (let [{:keys [app api]
                   :or {app {}
                        api {}}} (or routes {})]
              (debug "merging.." r)
              {:app (merge (:app r) app)
               :api (merge (:api r) api)}))]
    (debug "setting goldly routes: " routes)
    (reset! config-atom (transform [:webly :routes] m @config-atom))))

(defn set-webly-css-theme [css-theme]
  (let [{:keys [available current]} (get-in-config [:webly :theme])
        merged-theme {:available (merge available (:available css-theme))
                      :current  (merge current (:current css-theme))}]
    (swap! config-atom assoc-in [:webly :theme] merged-theme)))

(defn start-goldly-services [cljs-autoload-files]
  (let [goldly-autoload (get-in-config [:goldly :autoload-cljs-dir])
        w-cljs (start-cljs-watch (get-in-config [:goldly :watch-cljs-dir]))
        w-conn (start-ws-conn-watch)]
    (add-cljs-file-services cljs-autoload-files goldly-autoload)
    (write-target "goldly-run-sci-cljs-autoload" cljs-autoload-files)

    {:cljs-watch w-cljs
     :ws-watch w-conn
     :ns-clj (get-in-config [:ns-clj])
     :webly (get-in-config [:webly])}))

(defn start-goldly [config profile]
  ; start-goldly assumes config has already been loaded (done in services.edn)
  (let [{:keys [exts clj-require css-theme cljs-autoload-files]} (runtime-config (:goldly @config-atom))]
    (when (server? profile)
      (info "starting goldly server ..")
      (info "goldly clj requires: " clj-require)
      (require-namespaces clj-require)
      (info "setting goldly-routes ..")
      (set-webly-routes)
      (set-webly-css-theme css-theme)
      (write-extensions-runtime exts)
      (start-goldly-services cljs-autoload-files))))

(defn stop-goldly [{:keys [cljs-watch ws-watch]}]
  (info "stopping webly..")
  (when cljs-watch
    (warn "stopping goldly cljs-watch (not implemented)"))
  (when ws-watch
    (warn "stopping goldly ws-watch (not implemented)")))

;(defonce goldly-default-config (atom ["webly/config.edn" "goldly.edn"]))
;(add-config @goldly-default-config config)
