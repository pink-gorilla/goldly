(ns goldly.extension.sci
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [goldly.sci.bindings :refer [goldly-namespaces goldly-bindings goldly-ns-bindings add-cljs-bindings add-cljs-ns-bindings]]))

(defn add-extension-sci [{:keys [name
                                 cljs-namespace
                                 cljs-bindings cljs-ns-bindings]
                          :or {cljs-namespace []
                               cljs-bindings {}
                               cljs-ns-bindings {}}
                          :as extension}]

  (debug "cljs ns: " cljs-namespace)
  (doall (for [n cljs-namespace]
           (swap! goldly-namespaces conj [n])))

  (debug "cljs bindings: " cljs-bindings)
  (swap! goldly-bindings merge cljs-bindings)

  (debug "cljs ns-bindings: " cljs-ns-bindings)
  (swap! goldly-ns-bindings merge cljs-ns-bindings))

(defn make-lazy [bindings]
  (let [lazy-bindings (into {}
                            (map (fn [[k v]]
                                   [k (list 'wrap-lazy v)]) bindings))]
    (error "lazy bindings: " lazy-bindings)
    lazy-bindings))

(defn make-lazy-ns [ns-bindings]
  (into {}
        (map (fn [[k v]]
               [k (make-lazy v)]) ns-bindings)))

(defn add-extension-sci-lazy [{:keys [name
                                      cljs-namespace
                                      cljs-bindings cljs-ns-bindings]
                               :or {cljs-namespace []
                                    cljs-bindings {}
                                    cljs-ns-bindings {}}
                               :as extension}]

  (debug "cljs lazy bindings: " cljs-bindings)
  (swap! goldly-bindings merge (make-lazy cljs-bindings))

  (error "cljs lazy ns-bindings: " cljs-ns-bindings)
  (swap! goldly-ns-bindings merge (make-lazy-ns cljs-ns-bindings)))