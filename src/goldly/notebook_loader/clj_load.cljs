(ns goldly.notebook-loader.clj-load
  (:require
   [taoensso.timbre :as timbre :refer-macros [debugf info error]]
   [re-frame.core :as rf]
   [ui.notebook.loader.load :refer [load-notebook]]))

(rf/reg-event-fx
 :component/load
 (fn [cofx [_ type id]]
   (info "loading nb: " type id)
   (rf/dispatch [:ws/send [:component/load {:type type
                                            :id id}]
                 (fn [[t {:keys [id data type] :as doc}]]
                   (info "loaded nb: " id)
                   (rf/dispatch [:doc/load data]))
                 10000])
   nil))

(defmethod load-notebook :clj [{:keys [name type data]}]
  (rf/dispatch [:component/load :notebook name]))