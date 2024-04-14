(ns goldly.sci.loader.async-load
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [promesa.core :as p]
   [goldly.sci.loader.shadow-add :refer [add-shadow-module]]
   [goldly.sci.loader.cljs-source-add :refer [add-sci-cljs-source]]))

(defn async-load-fn
  [d]
  (let [r-p (p/deferred)
        shadow-p (add-shadow-module d)]
    (-> shadow-p 
        (p/then (fn [r]
                  (p/resolve! r-p r)))
        (p/catch (fn [err]
                   (let [source-p (add-sci-cljs-source d)]
                     (-> source-p
                        (p/then (fn [res] (p/resolve! r-p res)))
                        (p/catch (fn [err] (p/reject! r-p err))))))))
    
    ;(= libname "some_js_lib")
      ;(load-module-test ctx libname ns opts)
    r-p))
