(ns goldly.run.lazy-ext-css
  (:require-macros
   [goldly.app.build :refer [compiled-ext-fns theme-registry]])
  (:require
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof warn error errorf]]
   [clojure.set]
   [re-frame.core :as rf]
   [goldly.offline.old :refer [static? get-ext-static]]))

(def mapping-table (compiled-ext-fns))

(warn "compiled extension mappings:" (pr-str mapping-table))

(defn lazy? [{:keys [name lazy] :as ext}]
  lazy)

(defonce lazy-loaded-atom (atom #{}))

(defn loaded? [{:keys [name lazy] :as ext}]
  (or (not (lazy? ext))
      (contains? @lazy-loaded-atom name)))

(defn lookup-module [symbol-fn]
  (let [fn-name (name symbol-fn)]
    (get mapping-table fn-name)))

;; theme registry

(def themes (theme-registry))

(warn "compile-time lazy themes :" themes)

(defn load-css [ext-name]
  (info "layz-loading css for: " ext-name)
  (let [ext-theme (or (get themes ext-name)
                      {:available {}
                       :current {}})
        evt [:css/add-components (:available ext-theme) (:current ext-theme)]]
    (warn "theme for " ext-name ": " evt)
    (rf/dispatch evt)))

(defn goldly-on-load [symbol-fn]
  (infof "goldly lazy loading %s" symbol-fn)
  (if-let [ext-name (lookup-module symbol-fn)]
    (when-not (contains? @lazy-loaded-atom ext-name)
      (swap! lazy-loaded-atom conj ext-name)
      (load-css ext-name))
    (error "module not found in lookup table: " symbol-fn)))

(defn add-load-status
  "adds :loaded key to each ext in the seq
   used to display loading status"
  [ext-seq]
  (let [add-load (fn [x]
                   (assoc x :loaded (loaded? x)))]
    (into []
          (map add-load ext-seq))))
