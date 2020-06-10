(ns goldly.nrepl.client
  (:require
   [clojure.pprint :refer [pprint]]
   [nrepl.core :as nrepl]
   ;[goldly.nrepl.middleware]
   ))

(defn port-from-file []
  (try
    (Integer/parseInt (slurp ".nrepl-port"))
    (catch clojure.lang.ExceptionInfo e 0)
    (catch Exception e 0)))
;:nrepl-port-file (io/file (or (:nrepl-port-file conf) ".nrepl-port"))

(defn send-message
  [client on-receive-fn msg]
  (-> client
      (nrepl/message msg)
      doall
      on-receive-fn))


(def transport (atom nil))
(def client (atom nil))
(def session-id (atom nil))

(defn set-session-id [fragments]
  (when-not @session-id
    (when-let [f (first fragments)]
      (when-let [id (:new-session f)]
        (println "setting session id: " id)
        (reset! session-id id)))))

(defn on-eval [fragments]
  (println "\r\neval result:\r\n ")
  (set-session-id fragments)
  (doall (map-indexed (fn [i f]
                        (println i ": " (dissoc f :id #_:session))) fragments)))

(defn add-session-id [msg]
  (if @session-id (assoc msg :session @session-id)
      msg))

(defn send!
  [msg]
  (if @client
    (->> msg
         (add-session-id)
         (send-message @client on-eval))
    (println "cannot send nrepl msg. not connected!")))




(defn start! []
  (let [port (port-from-file)]
    (reset! transport (nrepl/connect :port port)) ; :host "172.18.0.5"
    (reset! client (nrepl/client @transport 15000)) ;  Long/MAX_VALUE
  ;(send! {:op "describe"})
    (send! {:op "clone"})
    (send! {:op "eval" :code "(require '[goldly.nrepl.middleware])"})
    (send! {:op "eval" :code "\"goldly snippet jack-in ..\""})
    (send! {:op "eval" :code "\"goldly snippet jack-in ..\""})
    ;(send! {:op "eval" :code "(require '[pinkgorilla.ui.hiccup_renderer])"})
    (send! {:op "add-middleware"
            :middleware ['goldly.nrepl.middleware/render-values
                       ;'goldly.nrepl.middleware/wrap-pinkie
                         ]})

    (println "goldly snippets connected successfully to nrepl port " port)
    (println "evals will be displayed at http://localhost:8000/#/snippets")))

(comment
  (start!)
  (clojure.core/ns-list)
  (send! {:op "eval" :code "(+ 8 8)"})

  (send! {:op "eval" :code "^:R [:p/vega (+ 8 8)]"})
  (send! {:op "eval" :code "(time (reduce + (range 1e6)))"})

  (send! {:op "pinkieeval" :code "^:R [:p (+ 8 8)]"})

  (send! {:op "describe"})
  (send! {:op "ls-sessions"})
  (send! {:op "ls-middleware"})

  ;; testing
  (port-from-file)
  (nrepl/connect :port 39719)
  (send! {:op "start-rebl-ui"})
  (send! {:op "close"})

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