(ns goldly.offline.app
  (:require
   [reagent.dom]
   [taoensso.timbre :refer-macros [info warn error]]
   [promesa.core :as p]
   [shadow.loader :as l]
   [modular.log]
   [goldly.sci.loader.static :refer [dynamic-base]]
   [goldly.sci.loader.cljs-source-http :as cljs-source]
   [goldly.sci.loader.shadow-module :as shadow-module]
   [goldly.sci.kernel-cljs :refer [require-async resolve-symbol]]))

; required in goldly.app.build

(defn mount-app [page-fn]
  (warn "mounting app: page-fn: " page-fn)
  (reagent.dom/render
   [page-fn]
   (.getElementById js/document "app")))

(defn patch-path []
  (cljs-source/set-github-load-mode)
  (l/init (dynamic-base)) ; prefix to the path loader
  ;(shadow-module/set-github-load-mode)
  )

(defn run-init-fn [symbol-init-as-string]
  (warn "starting with init-fn: " symbol-init-as-string)
  (let [init-symbol (symbol symbol-init-as-string)
        libspec (-> init-symbol namespace symbol)
        require-p (require-async libspec)]
    (-> require-p
        (p/then (fn [res]
                  (let [init-fn (resolve-symbol init-symbol)]
                    (if init-fn
                      (try
                        (init-fn)
                        (catch js/Exception ex
                          (error "Exception in init-fn: " ex)))
                      (error "could not resolve init-fn: " init-symbol)))))
        (p/catch (fn [err]
                   (error "exception in init-fn: " err))))))

(defn mount-page [symbol-page-as-string]
  (info "mounting page symbol:" symbol-page-as-string)
  (let [page-symbol (symbol symbol-page-as-string)
        libspec (-> page-symbol namespace symbol)
        require-p (require-async libspec)]
    (-> require-p
        (p/then (fn [res]
                  (let [page-fn (resolve-symbol page-symbol)]
                    (warn "page-fn resolved: " page-fn)
                    (if page-fn
                      (mount-app page-fn)
                      (error "could not resolve page: " page-symbol)))))
        (p/catch (fn [err]
                   (error "mounting " symbol-page-as-string " failed. error: " err))))))

(defn ^:export start
  ([symbol-page-as-string]
   (start symbol-page-as-string nil))
  ([symbol-page-as-string symbol-init-as-string]
   (enable-console-print!)
   (modular.log/timbre-config!
    {:min-level [[#{"*"} :info]]})
   (patch-path)
   (if symbol-init-as-string
     (-> (run-init-fn symbol-init-as-string)
         (p/then (mount-page symbol-page-as-string))
         (p/catch (fn [err]
                    (error "start exception: " err))))
     (mount-page symbol-page-as-string))))

