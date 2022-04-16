(ns goldly.scratchpad
  (:require
   [taoensso.timbre :refer [info]]
   [clojure.core.async :refer [go chan >! <!! >!! alts! timeout]]
   [reval.type.converter :refer [value->hiccup]]
   [modular.ws.msg-handler :refer [-event-msg-handler]]
   [modular.ws.core :refer [send-all! send-response connected-uids]]))

(defn clear! []
  (info "clearing scratchpad: ")
  (send-all! [:scratchpad/msg {:op :clear}])
  nil)

(defn ->hiccup [h]
  (if (vector? h)
    h
    (value->hiccup h)))

(defn show! [h-or-type]
  (let [h (->hiccup h-or-type)]
    (info "sending to scratchpad: " h)
    (send-all! [:scratchpad/msg {:op :show
                                 :hiccup h
                                 :ns (str *ns*)}])
    h))

(defonce chan-scratchpad-evalresult (chan))

(defn eval-str! [code-str]
   (info "evaluating code: " code-str)
   (send-all! [:scratchpad/msg {:op :eval
                                :code code-str
                                :ns (str *ns*)}])
   (<!!
    (go
      (let [[result source] (alts! [chan-scratchpad-evalresult (timeout 2500)])]
        (if (= source chan-scratchpad-evalresult)
          (println "Eval result: " result)
          (println "Eval Timeout!")))))
    nil)

(defmacro eval-code! [code-expr]
  (eval-str! (pr-str code-expr)))


(defn show-as [ui-kw & args]
  (let [h (into [ui-kw] args)]
    (show! h)
    h))

(defn get! []
  (send-all! [:scratchpad/get-state]))

(defonce chan-scratchpad-get (chan))

(defmethod -event-msg-handler :scratchpad/state
  [{:as ev-msg :keys [event]}]
  (let [[event-name data] event]
    (go (info "scratchpad data rcvd: " data)
        (>! chan-scratchpad-get data))
    nil))



(defmethod -event-msg-handler :scratchpad/evalresult
  [{:as ev-msg :keys [event]}]
  (let [[event-name data] event]
    (info "sci cljs eval result " data)
    (go 
      (>! chan-scratchpad-evalresult data))
    nil))






(comment
  (show! [:p "hello, scratchpad!"])

  (clear!)

  (eval-str! "(alert 134)")
  (eval-code! (alert 1234))
  (eval-code! (* 7 7 7))

  (eval-code! 
    (nav :user/main))
  
  (eval-code!
   (nav :scratchpad))
  
  


;  
  )