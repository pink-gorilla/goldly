(ns demo.cljs-libs.init
  (:require 
    [taoensso.timbre :as timbre :refer [info warn]]
    [re-frame.core :as rf]
    [webly.module.build :refer [load-namespace]]))

(rf/reg-event-db
 :goldly/init
 (fn [db [_]]
   (info "goldly init ..")
   (load-namespace 'reval.kernel.protocol)
   (load-namespace 'reval.kernel.cljs-sci)
   ;(info "available kernels: " (available-kernels))
   ; simulate a slow bundle load time, so we can see the ui
   (.setTimeout js/window (fn []
                            (info "webly demo started.")
                            (rf/dispatch [:webly/status :running])) 50)

   db))

