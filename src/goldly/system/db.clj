(ns goldly.system.db
  "runs goldly systems"
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warnf error errorf]]
   [goldly.system :refer [system->cljs]]))
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
                              :hidden (or (:hidden v) false)
                              :name (or (:name v) "")}) @systems))
        ;ids (keys @systems)
        ;ids (into [] (map name ids))
        ]
    [:goldly/systems #_ids summary]))

(defn system-response
  "gets system to be sent to clj"
  [id]
  (info "loading system id: " id)
  (let [id (keyword id)
        system (when id (id @systems))]
    (debug "loaded system id: " id " system:" system)
    (when system
      (system->cljs system))))

