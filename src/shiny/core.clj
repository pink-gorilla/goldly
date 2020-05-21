(ns shiny.core
  (:require
   [clojure.string]
   [shiny.ws :refer [send-all!]]))

(defn unique-id
  "Get a unique id."
  []
  (str (java.util.UUID/randomUUID)))


;; system

(def systems (atom {}))

(defn send-system-list []
  (let [systems (map :id @systems)]
    (send-all! [:shiny/systems systems])))

(defn send-event [system-id event-name & args]
  (let [message  {:system system-id :type type :args args}]
    (send-all! [:shiny/event message])))



(defn system->cljs [system]
  (let [system-cljs (dissoc system :clj)]
    (println "sending system to cljs: " system-cljs)))


(defmacro system [system-cljs system-clj]
  {:id (unique-id)
   :cljs (pr-str system-cljs)
   :clj system-clj})



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


