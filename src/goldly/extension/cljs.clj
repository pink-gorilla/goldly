(ns goldly.extension.cljs
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [webly.config :refer [config-atom]]
   [goldly.sci.bindings :refer [goldly-namespaces]]
   [goldly.extension.core :refer [lazy-enabled ext-lazy?]]
   [goldly.extension.sci :refer [add-extension-sci add-extension-sci-lazy]]
   [goldly.extension.pinkie :refer [add-extension-pinkie add-extension-pinkie-lazy]]))

(defn add-lazy-namespaces [name cljs-namespace]
  (let [modules-g (or (get-in @config-atom [:webly :modules])  {})
        module-m {(keyword name) (or cljs-namespace [])}
        modules (merge modules-g module-m)]
    (swap! config-atom assoc-in [:webly :modules] modules)))

(defn cljs-init []
  (when (lazy-enabled)
    (warn "lazy modules enabled!")
    (swap! goldly-namespaces conj '[webly.build.lazy :refer-macros [wrap-lazy]])))

(defn add-extension-cljs [{:keys [name cljs-namespace]
                           :or {cljs-namespace []}
                           :as ext}]
  (if (ext-lazy? ext)
    (do (warn "lazy extension: " name)
        (add-lazy-namespaces name cljs-namespace)
        (add-extension-sci-lazy ext)
        (add-extension-pinkie-lazy ext))
    (do
      (add-extension-sci ext)
      (add-extension-pinkie ext))))