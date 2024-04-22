(ns reval.init
   (:require
    [taoensso.timbre :as timbre :refer [info]]
    [webly.module.build :refer [load-namespace]]))

(defn reval-cljs-kernel-init []
  (info "goldly reval cljs-kernel-init ..")
  (load-namespace 'reval.kernel.protocol)
  (load-namespace 'reval.kernel.cljs-sci)
  nil)
