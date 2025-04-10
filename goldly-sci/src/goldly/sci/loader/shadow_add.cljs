(ns goldly.sci.loader.shadow-add
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [promesa.core :as p]
   [sci.core :as sci]
   [shadowx.module.build :refer [load-namespace-raw simple-namespace? assemble-simple-ns]]))

(defn load-shadow-ns [libname]
  (info "webly-shadow-ns load: " libname " type: " (type libname))
  (load-namespace-raw libname))

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
                  (if (simple-namespace? libname)
                    (let [x (assemble-simple-ns libname res)]
                      (info "ns " libname " is a simple namespace; adding ns..")
                      (sci/add-namespace! ctx libname x))
                    (do 
                       (info "ns " libname " is a sci-configs-namespace ; adding ns ..")
                       (sci/add-namespace! ctx libname res)))
                    ;; empty map return value, SCI will still process `:as` and `:refer`
                  (p/resolve! r-p {})))
        (p/catch (fn [err]
                   (warn "no shadow-module received for ns: " libname)
                   (p/reject! r-p err))))

    r-p))
