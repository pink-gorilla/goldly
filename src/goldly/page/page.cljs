(ns goldly.page.page
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [webly.web.handler :refer [reagent-page]]
   [pinkie.error :refer [error-boundary]]))

(defn add-page [p kw]
  (defmethod reagent-page kw [{:keys [route-params query-params handler] :as route}]
    [error-boundary
     [p route]]))

(defn available-pages []
  (->> (methods reagent-page)
       keys
       (remove #(= :default %))
       (into [])))