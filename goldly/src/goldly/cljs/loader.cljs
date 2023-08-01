(ns goldly.cljs.loader
  (:require
   [taoensso.timbre :refer-macros [trace debug debugf info infof warn warnf error errorf]]
   [cljs.core.async :refer [>! <! chan close! put!] :refer-macros [go]]
   [re-frame.core :as rf]
   [goldly.service.core :refer [run]]
   [goldly.sci.kernel-cljs :refer [compile-code compile-code-async]]
   [goldly.sci.error :refer [exception->error show-sci-error]]
   [goldly.cljs.reload :refer [reload-cljs]]
   [goldly.static :refer [cljs-explore get-code]]))

;; compile

#_(defn compile-cljs [{:keys [filename code]}]
    (debug "compiling: " filename)
    (let [r (compile-code code)]
      (if-let [e (:error r)]
        (show-sci-error filename e)
        (debugf "successfully compiled %s " filename))))

(defn compile-cljs-p [{:keys [filename code]}]
  (info "compiling-async: " filename)
  (let [er-p (compile-code-async code)]
    (-> er-p
        (.catch (fn [e]
                  (error "eval failed: " e)
                  (when-let [sci-err (exception->error e)]
                    (show-sci-error filename sci-err)
                    sci-err)))
        (.then (fn [er]
                 (infof "successfully compiled %s " filename)
                 (when [er]
                   (info "cljs eval result:" er)
                   ;(reset! cur-ns (:ns er))
                   er))))))

(defn compile-cljs [opts]
  (let [p (compile-cljs-p opts)
        ch (chan)]
    (.then p (fn [data]
               (infof "compile-code promise received:  %s" data)
               (put! ch 27)))
    ch))

;; websocket / static helper

(defn explore [static?]
  (if static?
    (cljs-explore)
    (run {:fun 'goldly.app.run/cljs-explore})))

(defn load-cljs-file [static? filename]
  (info "loading cljs file: " filename)
  (go
    (let [{:keys [error result] :as r}
          (<! (if static?
                (get-code filename)
                (run {:fun 'goldly.cljs.loader/load-file-or-res!
                      :args [filename]})))]
      (when error
        (error "error loading cljs: " r))
      (when result
        (<! (compile-cljs result))))))

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
            (info "autoload sci-cljs files: " (pr-str result))
            (loop [f (first result)
                   files (rest result)]
              (let [r (<! (load-cljs-file static? f))]
                (info "compile result: " r))
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
