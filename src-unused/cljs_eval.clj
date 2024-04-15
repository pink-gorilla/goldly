(ns reval.kernel.cljs-eval
  (:require
   [taoensso.timbre :refer [info]]
   [clojure.core.async :refer [go chan >! <!! alts! timeout]]
   [modular.ws.msg-handler :refer [-event-msg-handler]]
   [modular.ws.core :refer [send-all!]]))

(defonce chan-result (chan))

(defmethod -event-msg-handler :cljs/result
  [{:as _ev-msg :keys [event]}]
  (let [[_event-name data] event]
    (info "sci cljs eval result " data)
    (go
      (>! chan-result data))
    nil))

(defn eval-str! [code-str]
  (info "evaluating code: " code-str)
  (alter-var-root (var chan-result) (constantly (chan)))
  (send-all! [:cljs/eval {:code code-str
                          :ns (str *ns*)}])
  (<!!
   (go
     (let [[result source] (alts! [chan-result (timeout 2500)])]
       (if (= source chan-result)
         (println "Eval result: " result)
         (println "Eval Timeout!")))))
  nil)

(defmacro eval-code! [code-expr]
  (eval-str! (pr-str code-expr)))

(comment

  (eval-str! "(js/alert 134)")
  (eval-str! "(* 2 2 2)")
  (eval-str! "(* 3 3 3)")

  (eval-code! (js/alert 1234))
  (eval-code! (* 7 7 7))
  (eval-code! (* 2 2 2))

  (eval-code!
   (nav :user/main))

  (eval-code!
   (nav :scratchpad))

;  
  )