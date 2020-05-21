(ns shiny.core
  (:require
   [clojure.string]))

(defn unique-id
  "Get a unique id."
  []
  (str (java.util.UUID/randomUUID)))


;; system

(defn system->cljs [system]
  (let [system-cljs (dissoc system :clj)]
    (println "sending system to cljs: " system-cljs)))


(defmacro system [system-cljs system-clj]
  {:id (unique-id)
   :cljs (pr-str system-cljs)
   :clj system-clj})

(def systems (atom {}))

(defn on-event [[id name & args]]
  (println "rcvd event for system " id " " name)
  (let [system ((keyword id) @systems)
        f (when system ((keyword name) system))]
    (when f
      (println "executing " id name)
      (if args
        (apply f args)
        (f)))))

(defn dispatch [id name & args]
  (println "dispatching " id name)
  ; send to frontend
  )

(defn system-start!
  [route system]
  (println "starting system " (:id system) " at " route)
  (swap! systems assoc (keyword (:id system)) system)
  (system->cljs system))


