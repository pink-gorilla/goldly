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

(defn index-response [type]
  (infof "index/load type: %s " type)
  (let [data (load-index {:type type})
        response {:type type
                  :data data}]
    (debug "index response: " response)
    response))

(defmethod -event-msg-handler :index/load
  [{:as ev-msg :keys [event]}]
  (let [[event-name {:keys [type id]}] event
        data (index-response type)]
    (send-response ev-msg :index/store data)))

; on connection
(defn on-connect-send-systems [old new]
  (let [uids (:any new)
        data (index-response :system)
        response [:index/store data]]
    (info "uids connected: " uids)
    (doseq [uid uids]
      (info "sending systems info to: " uid)
      (debug "index response: " response)
      (send! uid response))))

(watch-conn on-connect-send-systems)

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


