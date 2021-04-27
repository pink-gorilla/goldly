(ns goldly.puppet.db
  "runs goldly systems"
  (:require
   [clojure.string]

   [taoensso.timbre :as log :refer [tracef debug debugf info infof warnf error errorf]]
   [webly.ws.core :refer [send-all! send! on-conn-chg]]
   [webly.ws.msg-handler :refer [-event-msg-handler]]
   [goldly.system :refer [system->cljs]]))


;; helper fns


(defn send-ws-response [{:as ev-msg :keys [id ?data ring-req ?reply-fn send-fn]}
                        goldly-tag
                        response]
  (let [session (:session ring-req)
        uid (:uid session)]
    (when (nil? ?reply-fn)
      (error "reply-fn is nil. this should not happen."))
    (if (nil? uid)
      (error "uid is nil. this should not happen.")
      (info "uid: " uid))
    (if response
      (cond
        ?reply-fn (?reply-fn [:goldly/system response])
        uid (send! uid [:goldly/system response])
        :else (error "Cannot send ws-response: neither ?reply-fn nor uid was set!"))
      (error "Can not send ws-response for nil response. " goldly-tag))))

(def systems (atom {}))

(defn get-system [id]
  (get @systems id))

(defn add-system [system]
  (swap! systems assoc  (:id system) system))

(defn systems-response []
  (let [;_ (println "systems-response: " @systems)
        summary (into []
                      (map (fn [[k v]]
                             {:id (name k)
                              :name (or (:name v) "")}) @systems))
        ;ids (keys @systems)
        ;ids (into [] (map name ids))
        ]
    [:goldly/systems #_ids summary]))

(defmethod -event-msg-handler :goldly/systems
  [ev-msg]
  (let [response (systems-response)]
    (send-ws-response ev-msg :goldly/system response)))

(defn system-response
  "gets system to be sent to clj"
  [id]
  (let [id (keyword id)
        system (when id (id @systems))]
    (info "loaded system id" id " system:" system)
    (when system
      (system->cljs system))))

(defmethod -event-msg-handler :goldly/system
  [{:as ev-msg :keys [event]}]
  (let [[event-name system-id] event]
    (let [response (or (system-response system-id) :g/system-nil)
          _ (info "sending system-response: " response)]
      (send-ws-response ev-msg :goldly/system response))))

(defn on-connect-send-systems [old new]
  (let [uids (:any new)]
    (info "uids connected: " uids)
    (doseq [uid uids]
      (info "sending systems info to: " uid)
      (send! uid (systems-response)))))

(reset! on-conn-chg on-connect-send-systems)
