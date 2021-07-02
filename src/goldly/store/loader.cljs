(ns goldly.store.loader
  (:require
   [taoensso.timbre :refer-macros [trace debug debugf info infof warn warnf error errorf]]
   [cljs.core.async :refer [>! <! chan close! put!] :refer-macros [go]]
   [re-frame.core :as rf]
   [webly.ws.msg-handler :refer [-event-msg-handler]]
   [goldly.service.core :refer [run]]
   [goldly.sci.kernel-cljs :refer [compile-code]]
   [goldly.sci.error :refer [show-sci-error]]))

(defn compile-cljs [{:keys [filename code]}]
  (info "compiling: " filename)
  (let [r (compile-code code)]
    (if (:error r)
      (show-sci-error filename r)
      (infof "successfully compiled %s " filename))))

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

; this does not trigger. I believe due to a bug in sente
#_(defmethod -event-msg-handler :goldly/cljs-sci-reload
    [{:keys [?data event data] :as ev-msg}] ;  ?data
    (let [[_ result] event]
  ; 
      (infof "cljs-sci-reload received: %s" ev-msg)
      (compile-cljs result)
 ; 
      ))

(rf/reg-event-fx
 :goldly/cljs-sci-reload
 (fn [cofx [_ result]]
   (infof "cljs-sci-reload received: %s" (:filename result))
   (compile-cljs result)
   nil))
