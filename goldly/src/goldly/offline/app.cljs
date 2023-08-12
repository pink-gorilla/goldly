(ns goldly.offline.app
  (:require
   [reagent.dom]
   [taoensso.timbre :refer-macros [info warn]]
   [shadow.loader :as l]
   [goldly.sci.loader.static :refer [dynamic-base]]
   [goldly.sci.loader.cljs-source-http :as cljs-source]
   [goldly.sci.loader.shadow-module :as shadow-module]
   [goldly.sci.kernel-cljs :refer [require-async resolve-symbol]]))

; required in goldly.app.build

(defn mount-app [page-fn]
  (reagent.dom/render
   [page-fn]
   (.getElementById js/document "app")))

(defn patch-path []
  (cljs-source/set-github-load-mode)
  (l/init (dynamic-base)) ; prefix to the path loader
  ;(shadow-module/set-github-load-mode)
  )

(defn ^:export start
  ([symbol-page-as-string]
   (start symbol-page-as-string nil))
  ([symbol-page-as-string symbol-init-as-string]
   (enable-console-print!)
   (patch-path)
   (when symbol-init-as-string
     (info "starting with init-fn: " symbol-init-as-string)
     (let [init-symbol (symbol symbol-init-as-string)
           libspec (-> init-symbol namespace symbol)
           require-p (require-async libspec)]
       (.then require-p (fn [res]
                          (let [init-fn (resolve-symbol init-symbol)]
                            (if init-fn
                              (init-fn)
                              (error "could not resolve init-fn: " init-symbol)))))
       (.catch require-p (fn [err]
                           (error "exception in init-fn: " err)))))
   (println "starting goldly static app page symbol:" symbol-page-as-string)
   (let [page-symbol (symbol symbol-page-as-string)
         libspec (-> page-symbol namespace symbol)
         require-p (require-async libspec)]
     (.then require-p (fn [res]
                        (let [page-fn (resolve-symbol page-symbol)]
                          (if page-fn
                            (mount-app page-fn)
                            (println "could not resolve page: " page-symbol)))))
     (.catch require-p (fn [err]
                         (println "could not sci-require ns: " libspec))))))

