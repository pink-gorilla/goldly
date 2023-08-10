(ns goldly.sci.loader.zrdz
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [sci.core :as sci]))

(defn load-module-test [ctx libname ns opts]
  (-> (js/Promise.resolve #js {:libfn (fn [] "result!")})
      (.then (fn [mod]
               (info "demo lib : " libname "did load: " mod "mod-clj:" (js->clj mod))
               (sci/add-class! ctx (:as opts) mod)
               (sci/add-import! ctx ns (:as opts) (:as opts))
               {:handled true}))))
