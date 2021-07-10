(ns goldly.extension.core
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error errorf]]
   [webly.writer :refer [write-status]]
   [webly.config :refer [config-atom]]
   [goldly.extension.pinkie :refer [pinkie-atom]]))

;; config
(defn lazy-enabled []
  (or (get-in @config-atom [:goldly :lazy]) false))

(defn lazy-excludes []
  (or (get-in @config-atom [:goldly :lazy-exclude]) #{}))

(defn lazy-excluded? [module-name]
  (let [excludes (lazy-excludes)]
    (contains? excludes module-name)))

(defn ext-lazy? [{:keys [name lazy]
                  :or {lazy false}}]
  (and (lazy-enabled)
       (not (lazy-excluded? name))
       lazy))

;; pinkie
(defn save-pinkie []
  (write-status "pinkie" @pinkie-atom))

; (defn lazy? [ext-name])

;; extensions

(defonce extension-atom (atom {}))

(defn extensions []
  @extension-atom)

(defn extension-names []
  (-> (keys @extension-atom)
      sort))

(defn extension-list []
  (into []
        (vals @extension-atom)))

(defn get-extension [name]
  (get @extension-atom name))

(defn extension-summary []
  (let [summary  (into []
                       (map
                        (fn [e]
                          (select-keys e [:name :lazy]))
                        (extension-list)))]
    (debug "ext sum: " summary)
    summary))

(defn print-all []
  (let [exts (extension-names)
        prn-ext (fn [name]
                  (let [x (get-extension name)]
                    (errorf "ext: %s data: %s" name x)))]
    (doall (map prn-ext exts))))

(defn- ext->map [extension-list]
  (into {}
        (map (fn [x] [(:name x) x]) extension-list)))

(defn- ext-ns [module-name binding-fns]
  (let [v (fn [s] [s module-name])
        el  (into []
                  (map v binding-fns))]
    el))

(defn coherent-bindings [{:keys [cljs-bindings cljs-ns-bindings] :as ext}]
  (merge cljs-ns-bindings {'user cljs-bindings}))

(defn binding-fns [bindings]
  (let [seq-ns-bindings (vals bindings)
        seq-fns (map vals seq-ns-bindings)]
    (apply concat seq-fns)))

(defn- ext->fns [{:keys [name cljs-bindings] :as ext}]
  (let [bindings (coherent-bindings ext)
        ;el (ext-ns name (vals cljs-bindings))
        el (ext-ns name (binding-fns bindings))]
    ;(error "bfn: " (pr-str (binding-fns bindings)))
    ;(error "el: " el)
    el))

(defn map-fn-module [exts]
  (into {}
        (apply concat (map ext->fns exts))))

(defn ext-fns []
  (let [exts (extension-list)]
    (map-fn-module exts)))

(defn ext-fns-s []
  (let [exts (ext-fns)]
    (into {}
          (map (fn [[k v]]
                 [(name k) v])
               exts))))

(defmacro compiled-ext-fns []
  (ext-fns-s))

(defn save-extensions [ext-list]
  (let [ext-map (ext->map ext-list)]
    (reset! extension-atom ext-map)
    (debug "extensions loaded: " (extension-names))
    ;todo: make unit tests of extension loader
    ;(error "ext: " @extension-atom)
    ;(error "ext list: " (extension-list))
    ;(error "ui-highlightjs: " (get-extension "ui-highlightjs"))
    ;(print-all)
    ;(error "ext fns:" (ext-fns))
    (info "extensions: " (extension-summary))
    (write-status "extensions" ext-list)
    (write-status "module-fn-maps" (map-fn-module ext-list))))





