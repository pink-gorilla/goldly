(ns goldly.app.run
  (:require
   [clojure.string] ; side effects
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [com.rpl.specter :refer [transform setval END ALL]]
   [modular.config :refer [get-in-config config-atom load-config! add-config resolve-config-key]]
   [modular.writer :refer [write-status write-target]]
   [modular.require :refer [require-namespaces]]
   [webly.build.profile :refer [server?]]
   [goldly.config.runtime :refer [runtime-config]]
   [goldly.run.cljs-load :refer [start-cljs-watch]]
   [goldly.run.ws-connect :refer [start-ws-conn-watch]]
   [goldly.run.services]
   [goldly.service.core]
   [goldly.service.expose :refer [start-services]]
   [goldly.cljs.loader]
   [modular.permission.service :refer [add-permissioned-services]]))

(defn write-extensions-runtime [ext-map]
  (->> ext-map
       vals
       (map #(dissoc %
                     :cljs-namespace  ; build-config
                     :cljs-ns-bindings))
       (sort-by :name)
       (write-target "goldly-run-ext")))

(def goldly-routes-default
  {:app {"" :goldly/no-page
         "goldly/reload" :goldly/reload-cljs}
   :api {}})

(defn write-api-routes-extensions [api-routes-extensions]
  (write-target "goldly-run-api-routes" api-routes-extensions))

(defn webly-routes [config api-routes-extensions]
  (let [{:keys [routes] :or {routes {}}} (get-in config [:goldly])
        routes (if (empty? routes)
                 (do (warn "no [:goldly :routes ] defined - you will see a blank page.")
                     {:app {"" :goldly/no-page}
                      :api {}})
                 routes)
        merged-routes
        {:app (merge (:app goldly-routes-default) (:app routes))
         :api (merge (:api goldly-routes-default) api-routes-extensions (:api routes))}]
    (write-api-routes-extensions merged-routes)
    merged-routes))

(defn webly-css-theme [config goldly-css-theme]
  (let [{:keys [available current]} (get-in config [:webly :theme])
        merged-theme {:available (merge available (:available goldly-css-theme))
                      :current  (merge current (:current goldly-css-theme))}]
    (write-target "goldly-run-theme-non-lazy" merged-theme)
    merged-theme))

(defn webly-config [config goldly-css-theme api-routes]
  (let [routes (webly-routes config api-routes)
        theme (webly-css-theme config goldly-css-theme)
        webly-config (-> (:webly config)
                         (assoc :routes routes
                                :theme theme))]
  ; TODO: refactor - config atom should not be used anymore here.
    (swap! config-atom assoc-in [:webly] webly-config)
    webly-config))

(defn add-cljs-file-services [ext-dirs goldly-autoload]
  (let [cljs-explore-fn #(goldly.run.cljs-load/cljs-files ext-dirs goldly-autoload)]
    (def cljs-explore cljs-explore-fn) ; needed so symbol can be exported in start-services
    (start-services
     {:name "cljs-file-services"
      :permission nil
      :symbols ['goldly.cljs.loader/load-file-or-res!
                'goldly.app.run/cljs-explore]})))

(defn start-goldly-services [config goldly-css-theme cljs-autoload-files api-routes]
  (let [goldly-autoload (get-in config [:goldly :autoload-cljs-dir])
        w-cljs (start-cljs-watch (get-in config [:goldly :watch-cljs-dir]))
        w-conn (start-ws-conn-watch)
        webly-config (webly-config config goldly-css-theme api-routes)]
    (add-cljs-file-services cljs-autoload-files goldly-autoload)
    (write-target "goldly-run-sci-cljs-autoload" cljs-autoload-files)
    ;(warn "dynamic webly-config: " webly-config)
    {:cljs-watch w-cljs
     :ws-watch w-conn
     :ns-clj (get-in-config [:ns-clj])
     :webly {:webly webly-config}}))

(defn expose-default-goldly-services []
  ; since goldly-service checks for each sub-serivce if it is permissioned,
  ; this means that goldly/service will be available to all users.
  (add-permissioned-services {:goldly/service nil})
  (start-services
   {:name "goldly-core-services"
    :permission nil
    :symbols [;'goldly.run.services/edn-load ; not used
              ; devtools:
              'goldly.run.services/goldly-version
              'goldly.run.services/extension-list ; not used
              'goldly.run.services/build-sci-config
              ; cljs loader
              'goldly.run.services/run-sci-cljs-autoload
              ; lazy-extension css loader
              ;goldly.run.services/get-extension-info  ; not used at all
              ; runtime
              'goldly.service.core/services-list
              ;'goldly.run.services/get-extension-info ; seems to be not used at all
              ]}))
(defn start-goldly [config profile]
  ; start-goldly assumes config has already been loaded (done in services.edn)
  (let [{:keys [exts clj-require css-theme cljs-autoload-files api-routes]} (runtime-config (:goldly @config-atom))]
    (info "starting goldly server ..")
    (info "goldly clj requires: " clj-require)
    (require-namespaces clj-require)
    (write-extensions-runtime exts)
    (expose-default-goldly-services)
    (start-goldly-services config css-theme cljs-autoload-files api-routes)))

(defn stop-goldly [{:keys [cljs-watch ws-watch]}]
  (info "stopping goldly..")
  (when cljs-watch
    (warn "stopping goldly cljs-watch (not implemented)"))
  (when ws-watch
    (warn "stopping goldly ws-watch (not implemented)")))

;(defonce goldly-default-config (atom ["webly/config.edn" "goldly.edn"]))
;(add-config @goldly-default-config config)
