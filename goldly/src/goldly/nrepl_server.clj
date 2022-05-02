(ns goldly.nrepl-server
  (:require
   [taoensso.timbre :as timbre :refer [infof warn errorf]]
   [nrepl.server]
   ;[pinkgorilla.nrepl.handler.nrepl-handler :refer [make-default-handler]]
   ;; side effects
   ;[nrepl.middleware.print]
  ;picasso
   ;[picasso.default-config]
   ;[pinkgorilla.notebook.repl]
   ;[picasso.datafy.file]
  ; nrepl-miiddleware
   ;[pinkgorilla.nrepl.middleware.picasso]
   ;[pinkgorilla.nrepl.middleware.sniffer]
   ))

(defn start-nrepl-server [config]
  (let [config (merge {:bind "0.0.0.0"
                       :port 9100}
                      (or config  {}))
        {:keys [bind port]} config]
    (infof "nrepl starting on %s:%s" bind port)
    (spit ".nrepl-port" (str port)) ; todo - add this to goldly!
    (nrepl.server/start-server :bind bind
                               :port port
                                ;:handler (make-default-handler)
                               )))

(defn stop-nrepl-server [this]
  (.close this))



