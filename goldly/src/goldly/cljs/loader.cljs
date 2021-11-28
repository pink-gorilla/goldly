(ns goldly.cljs.loader
  (:require
   [taoensso.timbre :refer-macros [trace debug debugf info infof warn warnf error errorf]]
   [cljs.core.async :refer [>! <! chan close! put!] :refer-macros [go]]
   [re-frame.core :as rf]
   [goldly.service.core :refer [run]]
   [goldly.sci.kernel-cljs :refer [compile-code]]
   [goldly.sci.error :refer [show-sci-error]]
   [goldly.cljs.reload :refer [reload-cljs]]
   [goldly.static :refer [cljs-explore get-code]]))

;; compile

(defn compile-cljs [{:keys [filename code]}]
  (info "compiling: " filename)
  (let [r (compile-code code)]
    (if (:error r)
      (show-sci-error filename r)
      (infof "successfully compiled %s " filename))))

;; websocket / static helper

(defn explore [static?]
  (if static?
    (cljs-explore)
    (run {:fun :cljs/explore})))

(defn load-cljs-file [static? filename]
  (info "loading cljs file: " filename)
  (go
    (let [{:keys [error result] :as r}
          (<! (if static?
                (get-code filename)
                (run {:fun :cljs/load :args [filename]})))]
      (when error
        (error "error loading cljs: " r))
      (when result
        (compile-cljs result)))))

; called from goldly.system.ws after ws connected:
(defn load-cljs [static?]
  (info "load-cljs static?: " static?)
  (go
    (let [{:keys [result error]} (<! (explore static?))]
      (if error
        (error "error getting cljs files: " (pr-str error))
        (if (empty? result)
          (warn "no autoload cljs files available!")
          (do
            (info "cljs files: " (pr-str result))
            (loop [f (first result)
                   files (rest result)]
              (<! (load-cljs-file static? f))
              (when (seq files)
                (recur (first files)
                       (rest files))))))))))

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
   (reload-cljs)
   nil))
