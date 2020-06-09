(ns goldy.nrepl.client
  (:require
   [clojure.pprint :refer [pprint]]
   [nrepl.core :as nrepl]
   [goldly.nrepl.middleware]))

(defn port-from-file []
  (try
    (Integer/parseInt (slurp ".nrepl-port"))
    (catch clojure.lang.ExceptionInfo e 0)
    (catch Exception e 0)))
;:nrepl-port-file (io/file (or (:nrepl-port-file conf) ".nrepl-port"))

(defn send-message
  ([conn msg]
   (send-message conn msg pprint))
  ([conn msg on-receive-fn]
   (-> (nrepl/client conn 5000)
       (nrepl/message msg)
       doall
       on-receive-fn)))


(def conn (atom nil))

(defn send! [msg]
  (if @conn
    (send-message @conn msg)
    (println "cannot send nrepl msg. not connected!")))

(defn start! []
  (reset! conn (nrepl/connect :port (port-from-file)))
  ;(send! {:op "describe"})
  (send! {:op "eval" :code "(require '[goldly.nrepl.middleware])"})
  (send! {:op "eval" :code "(require '[pinkgorilla.ui.hiccup_renderer])"})
  (send! {:op "add-middleware"
          :middleware ['goldly.nrepl.middleware/render-values
                       ;'goldly.nrepl.middleware/wrap-pinkie
                       ]})
  (send! {:op "eval" :code "\"pinkie render loaded!\""}))

(comment

  (start!)
  (+ 7 17)
  (println "hello, world!")
  (do (println "a") 2)
  {:a 1}
  ^:R [:p (+ 8 8)]
  ^:R [:p/vega (+ 8 8)]
  (pinkie.converter/R [:p/vega (+ 8 8)])
                      
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

  ;
  )