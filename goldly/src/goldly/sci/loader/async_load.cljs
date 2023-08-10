(ns goldly.sci.loader.async-load
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [goldly.sci.loader.shadow-module :as shadow-module]
   [goldly.sci.loader.cljs-source-http :as cljs-source]))

(defn async-load-fn
  [{:keys [libname opts ctx ns] :as d}]
  (let [sci-mod (shadow-module/sci-ns-lookup libname)]
    (cond
      ;(= libname "some_js_lib")
      ;(load-module-test ctx libname ns opts)

      sci-mod
      (shadow-module/load-module ctx libname opts ns sci-mod)

      :else
      (cljs-source/load-module-sci d))))
