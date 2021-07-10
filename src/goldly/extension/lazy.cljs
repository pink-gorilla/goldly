(ns goldly.extension.lazy
  (:require-macros [goldly.extension.core :refer [compiled-ext-fns]])
  (:require
   [clojure.set]
   [cljs.core.async :refer [>! <! chan close! put!] :refer-macros [go]]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof warn error errorf]]
   [webly.build.lazy :refer [on-load]]
   [goldly.service.core :refer [run]]))

(def mapping-table (compiled-ext-fns))

(info "compiled extension mappings:" (pr-str mapping-table))

(defn lazy? [{:keys [name lazy] :as ext}]
  lazy)

(defonce lazy-loaded-atom (atom #{}))

(defn loaded? [{:keys [name lazy] :as ext}]
  (or (not (lazy? ext))
      (contains? @lazy-loaded-atom name)))

(defn lookup-module [symbol-fn]
  (let [fn-name (name symbol-fn)]
    (get mapping-table fn-name)))

(defn load-css [ext-name]
  (warn "loading css for: " ext-name)
  (go
    (let [{:keys [error result] :as r} (<! (run {:fun :extension/theme
                                                 :args [ext-name]}))]
      (infof "theme rcvd %s" ext-name)
      ;(errorf "theme for %s is: %s" ext-name result)
      (rf/dispatch [:css/add-components
                    (:available result)
                    (:current result)]))))

(defn goldly-on-load [symbol-fn]
  (infof "goldly lazy loading %s" symbol-fn)
  (if-let [ext-name (lookup-module symbol-fn)]
    (when-not (contains? @lazy-loaded-atom ext-name)
      (swap! lazy-loaded-atom conj ext-name)
      (load-css ext-name))
    (error "module not found in lookup table: " symbol-fn)))

(reset! on-load goldly-on-load)

(defn add-load-status
  "adds :loaded key to each ext in te seq
   used to display loading status"
  [ext-seq]
  (let [add-load (fn [x]
                   (assoc x :loaded (loaded? x)))]
    (into []
          (map add-load ext-seq))))