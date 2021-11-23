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

(defn run-nrepl-server [config]
  (let [config (merge {:enabled false
                       :bind "0.0.0.0"
                       :port 9100}
                      (or config  {}))
        {:keys [enabled bind port]} config]
    (if enabled
      (do
        (infof "nrepl starting on %s:%s" bind port)
        (nrepl.server/start-server :bind bind
                                   :port port
                                   ;:handler (make-default-handler)
                                   )
        (spit ".nrepl-port" (str port)) ; todo - add this to goldly!
        )
      (do (warn "nrepl is disabled.")
          nil))))