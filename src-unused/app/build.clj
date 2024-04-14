(ns goldly.app.build
  (:require
   [clojure.string] ; side effects
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.config :refer [load-config! config-atom resolve-config-key]]
   [modular.writer :refer [write-status write-target]]
   [webly.build.profile :refer [setup-profile server?]]
   [webly.build.core :as webly]
   [goldly.config.build :refer [build-config]]
   [goldly.sci.bindings :refer [write-sci-binding-cljs-file]]))

;; for lazy extension css loading (on lazy load)

(defonce ext-fns-atom (atom {}))

(defmacro compiled-ext-fns []
  @ext-fns-atom)

(defn expose-compiled-ext [d]
  (let [d2 (into {}
                 (map (fn [[k v]]
                        [(name k) v])
                      d))]
    (reset! ext-fns-atom d2)))

(defonce theme-atom
  (atom {}))

#_(defonce theme-atom
    (atom {"ui-binaryclock" {:available {:clock {true ["binaryclock/clock.css"]}}
                             :current {:clock true}}}))

(defmacro theme-registry []
  @theme-atom)

(defn set-lazy-themes! [t]
  (reset! theme-atom t))

(defonce sci-lazy-atom (atom {}))

(defmacro sci-lazy-registry []
  @sci-lazy-atom)

(defn set-sci-lazy! [t]
  (reset! sci-lazy-atom t))

;; export webly config

(defn set-webly-config [cljs-config]
  (let [goldly-main (-> (:goldly-main cljs-config)
                        (conj 'goldly.run.app)
                        (conj 'goldly.offline.app)
                        vec)
        modules (dissoc cljs-config :goldly-main)]
    (swap! config-atom assoc-in [:webly :ns-cljs] goldly-main)
    (swap! config-atom assoc-in [:webly :modules] modules)))

(defn write-extensions-build [ext-map]
  (->> ext-map
       vals
       (map #(dissoc % :theme :clj-require :pinkie :autoload-clj-ns :autoload-cljs-dir))
       (sort-by :name)
       (write-target "goldly-build-ext")))

; sci-bindings-info

(defn- ext-ns [module-name binding-fns]
  ;(println "ext-ns" module-name)
  ;(println "ext-ns" module-name binding-fns)
  (->> (map (fn [s] [s module-name]) binding-fns)
       (into [])))

(defn fns-in-ns-map [ns-def-map]
  ;(println "fns-in-ns-map " ns-def-map)
  (->> ns-def-map
       vals))

(defn fns-in-ns [[ns-name ns-def-map-or-symbol]]
  ; map:
  ;  {'bar pinkgorilla.ui.viz.sparklines/sparkline-bar
  ;    'spot pinkgorilla.ui.viz.sparklines/sparkline-spot
  ;    'line pinkgorilla.ui.viz.sparklines/sparkline}
  ; symbol: sci.configs.reagent/reagent-ns
  ;(println "fns-in-ns: " ns-name ns-def-map-or-symbol)
  (if (map? ns-def-map-or-symbol)
    (fns-in-ns-map ns-def-map-or-symbol)
    [] ; (str ns-name "/cljs-dynamic")
    ))

#_(defn ns-defs [cljs-ns-bindings]
    (->> cljs-ns-bindings
         vals
         (map vals)))

(defn fns-in-nss [cljs-ns-bindings]
  ;(println "fns-in-nss input:  " cljs-ns-bindings)
  (let [r (map fns-in-ns cljs-ns-bindings)]
    ;(println "fns-in-nss result: " r)
    r))

(defn- ext->fns [{:keys [name cljs-ns-bindings]
                  :as ext}]
  (let [ext-fns (->> cljs-ns-bindings
                     (fns-in-nss)
                     (apply concat))]
    (ext-ns name ext-fns)))

(defn- ext-fn-lookup [ext-map]
  (->> ext-map
       vals
       (map ext->fns)
       (apply concat)
       (into {})))

;; build

(defn generate-goldly-build [goldly-config]
  (let [bconfig (build-config goldly-config)
        sci-bindings-filename (-> goldly-config
                                  :src-dir
                                  (or "src")
                                  (str "/goldly_bindings_generated.cljs"))
        ext-config (:exts bconfig)
        sci-config (:sci bconfig)
        cljs-config (:cljs bconfig)
        css-theme (:css-theme bconfig)
        ext-lookup (ext-fn-lookup ext-config)
        ns-module-lazy (:ns-module-lazy sci-config)]
    (write-target "goldly-build-full" bconfig)
    (write-target "goldly-build-sci-config" sci-config)
    (write-extensions-build ext-config)
    (write-sci-binding-cljs-file sci-config sci-bindings-filename)
    (expose-compiled-ext ext-lookup)
    (write-target "goldly-build-module-fn-maps" ext-lookup)
    (set-lazy-themes! css-theme)
    (write-target "goldly-build-theme-lazy" css-theme)
    (write-target "goldly-build-ns-module-lazy" ns-module-lazy)
    (set-sci-lazy! ns-module-lazy)
    (set-webly-config cljs-config)

    ; dump cljs autoload files. Useful for static version
    ;(discover-extensions)
    ;(generate-cljs-autoload)
    ;(export-sci-cljs)
    ))

(defn goldly-build [{:keys [config profile]}]
  (warn "loading config: " config)
  (load-config! config)
  ; extensions can add to cljs namespaces. therefore extensions have to
  ; be included at compile time. But extensions also are needed
  ; for css and clj ns. 
  (let [profile (setup-profile profile)]
    (when (:bundle profile)
      (let [goldly-config (get-in config [:goldly])]
        (generate-goldly-build goldly-config)
        (webly/build profile)))))

(comment
   ;(generate-goldly-build )

  (->> (build-config {:lazy true})
       :sci
       ;keys
                                        ;:ns-bindings

       keys
       ;:exts
       ;(ext-fn-lookup)
       ;(write-extensions-sci-bindings-info)
       )
  (generate-goldly-build {:lazy true})

; 
  )
