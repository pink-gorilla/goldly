(ns goldly.component.ws-connect
  (:require
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warnf error errorf]]
   [webly.ws.core :refer [send! send-response watch-conn]]
   [goldly.component.load :refer [get-index-response]]))

; on connection
(defn on-connect-send-systems [old new]
  (let [uids (:any new)
        system (get-index-response :system)
        notebook (get-index-response :notebook)]
    (infof "ws connected  uids: %s" uids)
    (doseq [uid uids]
      (infof "sending systems/notebooks to uid: %s" uid)
      (debug "index response: " system)
      (send! uid system)
      (send! uid notebook))))

(defn start-ws-conn-watch []
  (watch-conn on-connect-send-systems))