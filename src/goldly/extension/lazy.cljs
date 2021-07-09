(ns goldly.extension.lazy
  (:require-macros [goldly.extension.core :refer [compiled-ext-fns]])
  (:require
   [clojure.set]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info infof error errorf]]
   [webly.build.lazy :refer [on-load]]))

(def mapping-table (compiled-ext-fns))

(error "compiled mappings:" (pr-str mapping-table))

(defn lazy? [{:keys [name lazy] :as ext}]
  lazy)

(defonce lazy-loaded-atom (atom #{"ui-code"}))

(defn loaded? [{:keys [name lazy] :as ext}]
  (or (not (lazy? ext))
      (contains? @lazy-loaded-atom name)))

(defn lookup-module [symbol-fn]
  (let [fn-name (name symbol-fn)]
    (get mapping-table fn-name)))

(defn goldly-on-load [symbol-fn]
  (errorf "goldly lazy loading %s" symbol-fn)
  (if-let [ext (lookup-module symbol-fn)]
    (swap! lazy-loaded-atom conj ext)
    (error "module not found in lookup table: " symbol-fn)))

(reset! on-load goldly-on-load)

(defn add-load-status [ext-seq]
  (let [add-load (fn [x]
                   (assoc x :loaded (loaded? x)))]
    (into []
          (map add-load ext-seq))))