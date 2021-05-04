(ns systems.time
  (:require
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warn error errorf]]
   [webly.ws.core :refer [send-all!]]
   [webly.date :refer [now-str]]
   [clojure.core.async :as async  :refer [<! <!! >! >!! put! chan go go-loop]]
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly]
   [goldly.runner.clj-fn :refer [broadcast-to-system]]))

(system-start!
 (goldly/system
  {:id :time
   :state {:time "waiting for server time"}
   :html  [:div.rows
           [:p.text-xl.text-blue-700 "This demo shows how to push data from the server."]
           [:p.text-xl.text-blue-700 "time is pushed every 10 seconds."]
           [:div (:time @state)]]
   :fns   {}}

  {:fns {}}))

(defn start-time-pusher! []
  (go-loop []
    (<! (async/timeout 10000)) ; 10 seconds
    (let [snow (now-str)]
      (info "demo-system-time sending time: " snow)
      (broadcast-to-system :time snow [:time]))
    (recur)))

(start-time-pusher!)

; ws rcvd: id: :chsk/recv ?data: [:goldly/dispatch {:run-id nil, :system-id :time, :fun nil, :result "2021-04-27 05:26:53", :where [:time]}] taoensso.timbre.appenders.core.js:158:15
