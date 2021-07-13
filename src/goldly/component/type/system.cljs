(ns goldly.component.type.system
  (:require
   [taoensso.timbre :as timbre :refer-macros [debugf infof errorf]]
   [goldly.component.ui :refer [render-component]]
   [goldly.system.sci :refer [render-system]]))

(defmethod render-component :system [{:keys [id data args]}]
  (let [{:keys [fns-clj cljs]} @data
        {:keys [ext]} args]
    (infof "rendering system: id: %s data: %s args: %s" id @data args)
    [render-system (merge {:id id
                           :fns-clj fns-clj}
                          cljs) ext]))
