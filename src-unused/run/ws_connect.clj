(ns goldly.run.ws-connect
  (:require
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warn error errorf]]
   [modular.ws.core :refer [send! send-response watch-conn]]))

(defn on-connect-send [old new]
  (let [uids (:any new)]
    (infof "ws connected  uids: %s" uids)
    (doseq [uid uids]
      (infof "sending autoload-cljs to uid: %s" uid)
      ;(send! uid system)
      )))

(defn start-ws-conn-watch []
  (watch-conn on-connect-send))