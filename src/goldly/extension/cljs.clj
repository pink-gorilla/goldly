(ns goldly.extension.cljs
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [webly.config :refer [config-atom]]
   [goldly.sci.bindings :refer [goldly-namespaces]]
   [goldly.extension.core :refer [lazy-enabled lazy-excluded?]]
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

(defn add-extension-cljs [{:keys [name lazy cljs-namespace]
                           :or {lazy false
                                cljs-namespace []}
                           :as extension}]
  (if (and (lazy-enabled)
           (not (lazy-excluded? name))
           lazy)
    (do (error "lazy extension: " name)
        (add-lazy-namespaces name cljs-namespace)
        (add-extension-sci-lazy extension)
        (add-extension-pinkie-lazy extension))
    (do
      (add-extension-sci extension)
      (add-extension-pinkie extension))))