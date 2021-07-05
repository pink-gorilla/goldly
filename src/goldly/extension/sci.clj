(ns goldly.extension.sci
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [goldly.sci.bindings :refer [add-cljs-namespace goldly-namespaces goldly-bindings goldly-ns-bindings add-cljs-bindings add-cljs-ns-bindings]]))

(defn add-extension-sci [{:keys [name
                                 cljs-namespace
                                 cljs-bindings cljs-ns-bindings]
                          :or {cljs-namespace []
                               cljs-bindings {}
                               cljs-ns-bindings {}}
                          :as extension}]

  (debug "cljs ns: " cljs-namespace)
  (doall (for [n cljs-namespace]
           ;(add-cljs-namespace [n])
           (swap! goldly-namespaces conj [n])))

  (debug "cljs bindings: " cljs-bindings)
  ;(add-cljs-bindings cljs-bindings)
  (swap! goldly-bindings merge cljs-bindings)

  (debug "cljs ns-bindings: " cljs-ns-bindings)
  ;(doall (for [[k v] cljs-ns-bindings]
  ;         (add-cljs-ns-bindings k v)))
  (swap! goldly-ns-bindings merge cljs-ns-bindings))