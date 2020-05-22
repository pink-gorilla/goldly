(ns shiny.core
  (:require
   [clojure.string]
   [clojure.core.async :as async  :refer (<! <!! >! >!! put! chan go go-loop)]
   [taoensso.timbre :as log :refer (tracef debugf infof warnf errorf)]
   [shiny.ws :refer [send-all! -event-msg-handler]]))

(defn unique-id
  "Get a unique id."
  []
  (str (java.util.UUID/randomUUID)))


;; system

(def systems (atom {}))

(defn systems-response []
  (let [;_ (println "systems: " @systems)
        ids (keys @systems)
        ids (into [] (map name ids))]
    [:shiny/systems ids]))

(defn system-response [id]
  (let [id (keyword id)
        s (id @systems)]
    (if s (dissoc s :clj) {})))

(defn send-event [system-id event-name & args]
  (let [message  {:system system-id :type event-name :args args}]
    (send-all! [:shiny/event message])))



(defn system->cljs [system]
  (let [system-cljs (dissoc system :clj)]
    (println "sending system to cljs: " system-cljs)))



(defmacro system [{:keys [state html fns] :as system-cljs} system-clj]
  (let [fns (zipmap (keys fns)
                    (map #(pr-str %) (vals fns)))]
  {:id (unique-id)
   :cljs {:state state
          :html (pr-str html)
          :fns (pr-str fns)}
   :clj system-clj}))

(comment
  (macroexpand (system {:html [:h1] :fns {:a 6 :b 8 :c "g"} :state 9} 2))
  
  )


(defmethod -event-msg-handler :shiny/systems
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid (:uid session)]
    (tracef "systems event: %s" event)
    (when ?reply-fn
      (?reply-fn (systems-response)))))

(defmethod -event-msg-handler :shiny/system
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid (:uid session)
        [event-name system-id] event
        ]
    (infof "system event: %s %s" event-name system-id)
    (when ?reply-fn
      (?reply-fn (system-response system-id)))))


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

(defn system-start!
  [route system]
  (println "starting system " (:id system) " at " route)
  (swap! systems assoc (keyword (:id system)) system)
  (system->cljs system))


(def broadcast-enabled?_ (atom true))

(defn start-heartbeats!
  "setup a loop to broadcast an event to all connected users every second"
  []
  (go-loop [i 0]
    (<! (async/timeout 10000))
    (when @broadcast-enabled?_ (send-all! (systems-response)))
    (recur (inc i))))

(start-heartbeats!)

