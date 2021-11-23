(ns goldly.page.page
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [frontend.page :refer [reagent-page]]
   [pinkie.error :refer [error-boundary]]))

(defn show-page
  "shows a page 
   expects: kw and route-map"
  [route-map]
  (reagent-page route-map))

;; this is already in webly.
(defn available-pages
  "currently available pages that can be used in the routing table
   seq of page keywords"
  []
  (->> (methods reagent-page)
       keys
       (remove #(= :default %))
       (into [])))

(defn add-page
  "add-page is exposed to sci
   defines a new browser-based page 
   that can be used in the routing table to define new pages"
  [p kw]
  (defmethod reagent-page kw [{:keys [route-params query-params handler] :as route}]
    [error-boundary
     [p route]]))


