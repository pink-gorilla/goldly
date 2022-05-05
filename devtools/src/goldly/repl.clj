(ns goldly.repl
  (:require
   [taoensso.timbre :refer [info]]
   [clojure.core.async :refer [go chan >! <!! >!! alts! timeout]]
   [reval.type.converter :refer [value->hiccup]]
   [modular.ws.msg-handler :refer [-event-msg-handler]]
   [modular.ws.core :refer [send-all! send-response connected-uids]]))

(defn clear! []
  (info "clearing repl: ")
  (send-all! [:repl/msg {:op :clear}])
  nil)

(defn ->hiccup [h]
  (if (vector? h)
    h
    (value->hiccup h)))

(defn show! [h-or-type]
  (let [h (->hiccup h-or-type)]
    (info "sending to repl: " h)
    (send-all! [:repl/msg {:op :show
                           :hiccup h
                           :ns (str *ns*)}])
    h))

(defonce chan-repl-evalresult (chan))

(defn eval-str! [code-str]
  (info "evaluating code: " code-str)
  (send-all! [:repl/msg {:op :eval
                         :code code-str
                         :ns (str *ns*)}])
  (<!!
   (go
     (let [[result source] (alts! [chan-repl-evalresult (timeout 2500)])]
       (if (= source chan-repl-evalresult)
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
  (send-all! [:repl/get-state]))

(defonce chan-repl-get (chan))

(defmethod -event-msg-handler :repl/state
  [{:as ev-msg :keys [event]}]
  (let [[event-name data] event]
    (go (info "repl data rcvd: " data)
        (>! chan-repl-get data))
    nil))

(defmethod -event-msg-handler :repl/evalresult
  [{:as ev-msg :keys [event]}]
  (let [[event-name data] event]
    (info "sci cljs eval result " data)
    (go
      (>! chan-repl-evalresult data))
    nil))

(comment
  (show! [:p "hello, repl!"])

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