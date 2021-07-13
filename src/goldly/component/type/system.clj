(ns goldly.component.type.system
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warnf error errorf]]
   [goldly.component.load :refer [load-index load-component]]
   [goldly.system :refer [system->cljs]]))
(def systems (atom {}))

(defn get-system [id]
  (get @systems id))

(defn add-system [system]
  (swap! systems assoc  (:id system) system))

;; index
(defn systems-response []
  (into []
        (map (fn [[k v]]
               {:id (name k)
                :hidden (or (:hidden v) false)
                :name (or (:name v) "")}) @systems)))

(defmethod load-index :system [{:keys [type]}]
  (let [summary (or (systems-response)
                    {:type type
                     :status :g/system-nil})
        _ (debug "sending system-response: " summary)]
    summary))

;; component

(defn system-response
  "gets system to be sent to clj"
  [id]
  (debug "loading system id: " id)
  (let [id (keyword id)
        system (when id (id @systems))]
    (debug "loaded system id: " id " system:" system)
    (when system
      (system->cljs system))))

(defmethod load-component :system [{:keys [id type]}]
  (let [response (or (system-response id)
                     {:id id
                      :status :g/system-nil})
        _ (debug "sending system-response: " response)]
    response))

