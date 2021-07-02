(ns goldly.store.loader
  (:require
   [taoensso.timbre :refer-macros [trace debug debugf info infof warn warnf error errorf]]
   [cljs.core.async :refer [>! <! chan close! put!] :refer-macros [go]]
   [goldly.service.core :refer [run]]
   [goldly.sci.kernel-cljs :refer [compile-code]]))

(defn compile-cljs [{:keys [filename code]}]
  (info "compiling: " filename)
  (compile-code code))

(defn load-cljs-file [filename]
  (info "loading cljs file: " filename)
  (go
    (let [{:keys [error result] :as r} (<! (run {:fun :cljs/load :args [filename]}))]
      (when error
        (warn "error loading cljs: " r))
      (when result
        (compile-cljs result)))))
; called from goldly.system.ws after ws connected:
(defn load-cljs []
  (info "load-cljs")
  (go
    (let [files-result (<! (run {:fun :cljs/explore}))
          files (:result files-result)]
      (info "cljs files: " (pr-str files))
      (doall
       (for [f files]
         (load-cljs-file f)))
;      
      )))

