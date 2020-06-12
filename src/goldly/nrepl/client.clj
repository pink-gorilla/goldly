(ns goldly.nrepl.client
  "a simple nrepl client.
   Uses :op clone to keep the same session for multiple requests.
   stateless implementation.
   After each req all eval res are pushed to callback-fn.
   Usecase
     (connect! port) - returns state atom
     (send! state message)
     (send! state message) - so multiple calls possible
   
   todo: 
   - change bencode transport to edn transport?
   - take interface from system / nrepl ?
   - convert to async channels ?   
   "

  (:require
   [clojure.pprint :refer [pprint]]
   [nrepl.core :as nrepl]
   [goldly.nrepl.logger :refer [new-log-session!]]))

(defn- set-session-id! [state fragments]
  ;(println "set-session-id!")
  (when-not (:session-id @state)
    (when-let [f (first fragments)]
      (when-let [id (:new-session f)]
        (println "setting session id: " id)
        (swap! state assoc :session-id id)
        (new-log-session! id)))))

(defn- add-session-id [state msg]
  (if-let [session-id (:session-id @state)]
    (assoc msg :session session-id)
    msg))

(defn process-responses [state on-receive-fn fragments]
  (set-session-id! state fragments)
  (on-receive-fn fragments)
  :nrepl-rep-rcvd)

(defn send!
  [state on-receive-fn msg]
  ;(println "send! msg" msg " state: " @state " fn: " on-receive-fn)
  (if-let [client (:client @state)]
    (->> (add-session-id state msg)
         (nrepl/message client)
         doall
         (process-responses state on-receive-fn))
    (println "cannot send nrepl msg. not connected!")))

(defn connect! [port]
  (let [transport (nrepl/connect :port port)  ; :host "172.18.0.5"
        client (nrepl/client transport Long/MAX_VALUE)  ; 15000 
        state (atom {:transport transport
                     :client client
                     :session-id nil})]
    (send! state pprint {:op "clone"})
    state))

(comment

  (def s (connect! 33117))
  @s
  (send! s pprint {:op "ls-sessions"})
  (send! s pprint {:op "describe"})
  (send! s pprint {:op "ls-middleware"})
  (send! s pprint {:op "close"})

  ; testing
  (def s (nrepl/connect :port 39719))


 ; 
  )