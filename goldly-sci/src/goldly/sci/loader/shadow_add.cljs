(ns goldly.sci.loader.shadow-add
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [promesa.core :as p]
   [sci.core :as sci]
   [goldly.sci.loader.shadow-load :refer [load-shadow-ns]]))


(defn add-shadow-module [{:keys [ctx libname ns opts sci-mod]}]
  (info "load-shadow-module: ns: " libname)
  (let [r-p (p/deferred)
        shadow-p (load-shadow-ns libname)
        shadow-p (if (p/promise? shadow-p)
                     shadow-p
                   (p/resolved shadow-p))]
    (-> shadow-p
        (p/then (fn [res]
                   (info "received shadow-module for libname: " libname "ns: " ns)
                   (sci/add-namespace! ctx libname res)
                    ;; empty map return value, SCI will still process `:as` and `:refer`
                   (p/resolve! r-p {})))
        (p/catch (fn [err]
                 (error "no shadow-module received for ns: " libname)
                  (p/reject! r-p err)
                 ))
        )
    r-p))
    