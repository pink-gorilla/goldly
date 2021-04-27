(ns systems.time
  (:require
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warn error errorf]]
   [webly.ws.core :refer [send-all!]]
   [webly.date :refer [now-str]]
   [clojure.core.async :as async  :refer [<! <!! >! >!! put! chan go go-loop]]
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly]))

(system-start!
 (goldly/system
  {:id :time
   :state {:time "waiting for server time"
           :msg "Type Something..."}
   :html  [:div.rows
           [:h1 "Time:"]
           [:div (:time @state)]]
   :fns   {}}

  {:fns {}}))

(defn start-time-pusher! []
  (go-loop []
    (<! (async/timeout 10000)) ; 10 seconds
    (let [snow (now-str)]
      (warn "demo-system-time sending time: " snow)
      (send-all! [:goldly/dispatch {:run-id 1
                                    :system-id 1
                                    :fun :f
                                    :result snow
                                    :where [:time]}]))
    (recur)))

(start-time-pusher!)
