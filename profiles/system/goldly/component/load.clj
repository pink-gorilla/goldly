(ns goldly.component.load
  (:require
   [taoensso.timbre :as log :refer [tracef debug debugf info infof warnf error errorf]]
   [webly.ws.core :refer [send! send-response watch-conn]]
   [webly.ws.msg-handler :refer [-event-msg-handler]]))

; index

(defmulti load-index (fn [m] (:type m)))

(defmethod load-index :default [{:keys [id type]}]
  {:id id
   :error (str "type unknown: " type)})

(defn get-index [type]
  (infof "index/load type: %s " type)
  (let [data (load-index {:type type})
        response {:type type
                  :data data}]
    (debug "index response: " response)
    response))

(defmethod -event-msg-handler :index/load
  [{:as ev-msg :keys [event]}]
  (let [[event-name {:keys [type id]}] event
        data (get-index type)]
    (send-response ev-msg :index/store data)))

(defn get-index-response [type]
  [:index/store (get-index type)])

; component
(defmulti load-component (fn [m] (:type m)))

(defmethod load-component :default [{:keys [id type]}]
  {:id id
   :error (str "type unknown: " type)})

(defmethod -event-msg-handler :component/load
  [{:as ev-msg :keys [event]}]
  (let [[event-name {:keys [type id]}] event
        _ (infof "component/load type: %s id: %s" type id)
        response (load-component {:id id
                                  :type type})]
    (debug "component load: " response)
    (send-response ev-msg
                   :component/store
                   (assoc response :type type))))


