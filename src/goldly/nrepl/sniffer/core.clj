(ns goldly.nrepl.sniffer.core
  (:require
   [goldly.nrepl.client :as client]
   ;[goldly.nrepl.sniffer.middleware]
   ))

(defn port-from-file []
  (try
    (Integer/parseInt (slurp ".nrepl-port"))
    (catch clojure.lang.ExceptionInfo e 0)
    (catch Exception e 0)))

(defn- print-eval-result [fragments]
  (doall
   (map-indexed
    (fn [i f]
      (println i ": " (dissoc f :id #_:session))) fragments)))

(def state-a (atom nil))

(defn current-session-id []
  (when-let [state @state-a]
    (when-let [session-id (:session-id @state)]
      session-id)))

(defn send! [msg]
  (if-let [send-fn (:send-fn @state-a)]
    (do
      ;(println "sniffer state: " @state-a)
      (send-fn msg))
    (println "send failed - not started")))

(defn start-sniffer! []
  (println "goldly start-sniffer! ..")
  (let [port (port-from-file)
        state (client/connect! port)]
    (reset! state-a {:send-fn (partial client/send! state print-eval-result)
                     :state state})
  ;(send! {:op "describe"})

    (send! {:op "eval" :code "(require '[goldly.nrepl.sniffer.middleware])"})
    ;(send! {:op "eval" :code "\"goldly snippet jack-in ..\""})
    ;(send! {:op "eval" :code "(require '[pinkgorilla.ui.hiccup_renderer])"})
    (send! {:op "add-middleware"
            :middleware ['goldly.nrepl.sniffer.middleware/render-values
                       ;'goldly.nrepl.middleware/wrap-pinkie
                         ]})

    (println "goldly snippets connected successfully to nrepl port " port)
    (println "evals will be displayed at http://localhost:8000/#/snippets")))

(comment

  (start-sniffer!)

  (send! {:op "ls-sessions"})
  (send! {:op "eval" :code "(+ 8 8)"})

  (send! {:op "eval" :code "^:R [:p/vega (+ 8 8)]"})
  (send! {:op "eval" :code "(time (reduce + (range 1e6)))"})

  (send! {:op "pinkieeval" :code "^:R [:p (+ 8 8)]"})

  (send! {:op "start-rebl-ui"})


  ;; testing


  (port-from-file)


  ; "clone", which will cause a new session to be retained. 
  ; The ID of this new session will be returned in a response message 
  ; in a :new-session slot. The new session's state (dynamic scope, etc)
  ;  will be a copy of the state of the session identified in 
  ;  the :session slot of the request.
  ; (if-let [new-session (:new-session message)]
  ; "interrupt", which will attempt to interrupt the current execution with id provided in the :interrupt-id slot.
   ; "close", which drops the session indicated by the ID in the :session slot. The response message's :status will include :session-closed.
   ; "ls-sessions", which results in a response message containing a list of the IDs of the currently-retained sessions in a :session slot.       
  ;
  )