(ns goldly.ws-connect
  (:require
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warn error errorf]]
   [modular.ws.core :refer [send! send-response watch-conn]]
   ;[goldly.component.load :refer [get-index-response]]
   ))

; on connection
(defn on-connect-send-systems [old new]
  (let [uids (:any new)
        ;system (get-index-response :system)
        ;notebook (get-index-response :notebook)
        ]
    (infof "ws connected  uids: %s" uids)
    (doseq [uid uids]
      (infof "sending autoload-cljs to uid: %s" uid)
      ;(debug "system response: " system)
      ;(warn "notebook response: " notebook)
      ;(send! uid system)
      ;(send! uid notebook)
      )))

(defn start-ws-conn-watch []
  (watch-conn on-connect-send-systems))