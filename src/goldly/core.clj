(ns goldly.core
  (:require
   [clojure.string]
   [clojure.core.async :as async  :refer (<! <!! >! >!! put! chan go go-loop)]
   [taoensso.timbre :as log :refer (tracef debugf info infof warnf errorf)]
   [goldly.ws :refer [send-all! chsk-send! -event-msg-handler connected-uids]]))

(defn unique-id
  "Get a unique id."
  []
  (str (java.util.UUID/randomUUID)))

;; system

(def systems (atom {}))

(defn systems-response []
  (let [;_ (println "systems-response: " @systems)
        summary (into []
                      (map (fn [[k v]]
                       {:id (name k)
                        :name (or (:name v) "")}
                       ) @systems ))
        ;ids (keys @systems)
        ;ids (into [] (map name ids))
        ]
    [:goldly/systems #_ids summary]))

(defn system->cljs
  "converts a system from clj to cljs"
  [system]
  (let [clj (or (get-in system [:clj :fns]) {})
        system-cljs (-> system
                        (dissoc :clj)
                        (assoc :fns-clj (into [] (keys clj))))]

    (println "system-cljs: " system-cljs)
    system-cljs))


(defn system-response
  "gets system to be sent to clj"
  [id]
  (let [id (keyword id)
        system (or (id @systems) {})]
    (system->cljs system)))

(defn send-event [system-id event-name & args]
  (let [message  {:system system-id :type event-name :args args}]
    (send-all! [:goldly/event message])))



(defn into-mapper
  [f m]
  (into (empty m) (for [[k v] m] [k (f v)])))

(defmacro system [{:keys [name state html fns] :as system-cljs} system-clj]
  (let [fns (zipmap (keys fns)
                    (map #(pr-str %) (vals fns)))]
    {:id (unique-id)
     :name name
     :cljs {:state state
            :html (pr-str html)
            :fns (into-mapper pr-str fns)}
     :clj system-clj}))

(comment
  (macroexpand (system {:html [:h1] :fns {:a 6 :b 8 :c "g"} :state 9} 2)))


(defmethod -event-msg-handler :chsk/uidport-open
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [;session (:session ring-req)
        ;uid (:uid session)
        ]
    (info ":chsk/uidport-open: %s" ev-msg)
    #_(when ?reply-fn
        (?reply-fn (systems-response)))))

(defmethod -event-msg-handler :chsk/uidport-close
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [;session (:session ring-req)
        ;uid (:uid session)
        ]
    (info ":chsk/uidport-close: %s" ev-msg)
    #_(when ?reply-fn
      (?reply-fn (systems-response)))))



(defmethod -event-msg-handler :goldly/systems
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid (:uid session)]
    (tracef "systems event: %s" event)
    (when ?reply-fn
      (?reply-fn (systems-response)))))

(defmethod -event-msg-handler :goldly/system
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid (:uid session)
        [event-name system-id] event]
    (infof "system event: %s %s" event-name system-id)
    (if ?reply-fn
      (?reply-fn (system-response system-id))
      (chsk-send! uid [:goldly/system (system-response system-id)]))))

(defn on-event [[id name & args]]
  (println "rcvd event for system " id " " name)
  (let [system ((keyword id) @systems)
        f (when system ((keyword name) system))]
    (when f
      (println "executing " id name)
      (if args
        (apply f args)
        (f)))))

(defn dispatch [system-id event-name & args]
  (println "dispatching " system-id event-name)
  (send-event system-id event-name args))

(add-watch connected-uids :connected-uids
           (fn [_ _ old new]
             (when (not= old new)
               (infof "Connected uids change: %s" new)
               (let [uids (:any new)]
                 (info "uids: " uids)
                 (doseq [uid uids]
                   (info "sending systems info to: " uid)
                   (chsk-send! uid (systems-response)))))))

(defn system-start!
  [system]
  (println "starting system " (:id system))
  (swap! systems assoc (keyword (:id system)) system)
  (system->cljs system))

(def broadcast-enabled?_ (atom true))

(defn start-heartbeats!
  "setup a loop to broadcast an event to all connected users every second"
  []
  (go-loop [i 0]
    (<! (async/timeout 60000))
    (when @broadcast-enabled?_ (send-all! (systems-response)))
    (recur (inc i))))

;(start-heartbeats!)

