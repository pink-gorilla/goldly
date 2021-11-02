(ns goldly.component.load
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre :refer-macros [debug debugf info infof error errorf]]))

; index

(rf/reg-event-db
 :index/store
 (fn [db [_ {:keys [type data] :as x}]]
   (let [index (:index db)]
     (infof "index/store type: %s" x)
     (debugf "index/store type: %s data: %s" type data)
     (-> (if (nil? index)
           (assoc db :index {})
           db)
         (assoc-in [:index type] data)))))

(rf/reg-sub
 :index/show
 (fn [db [_ type]]
   (let [d (get-in db [:index type])]
     ;(info "showing index type: " type)
     ;(debug "showing index type: " type "data: " d)
     d)))

; component

(rf/reg-event-db
 :component/store
 (fn [db [_ {:keys [id type] :as data}]]
   (let [component (:component db)]
     (infof "component/store type: %s id: %s" type id)
     (-> (if (nil? component)
           (assoc db :component {})
           db)
         (assoc-in [:component type id] data)))))

(rf/reg-sub
 :component/show
 (fn [db [_ type id]]
   (get-in db [:component type id])))