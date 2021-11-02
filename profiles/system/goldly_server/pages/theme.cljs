(ns goldly-server.pages.theme
  (:require
   [taoensso.timbre :as timbre :refer-macros [info error infof errorf]]
   [re-frame.core :as rf]
   [webly.web.handler :refer [reagent-page]]
   [goldly-server.helper.site :refer [header]]))

;; css links
(defn show-css-links [css-links]
  (let [css-links (or css-links [])]
    (into [:div]
          (map (fn [n] [:span.m-1 n]) css-links))))

;; css theme by component

(defn comp-select [available k v]
  (let [o (or (keys (k available)) [v])
        on-change (fn [e]
                    (let [t (.-target e)
                          nv (.-value t)
                          nkv (keyword nv)]
                      (infof "setting component: %s theme: %s" k nv)
                      (rf/dispatch [:css/set-theme-component k nv])))]
    ;(error "avail: " o)
    (into [:select {:value  v
                    :on-change on-change}]
          (map (fn [o]
                 [:option {:value  o}
                  (str o)])
               o))))

(defn show-theme [{:keys [available current] :as theme}]
  [:table
   (into [:tbody
          [:tr
           [:td "component"]
           [:td "theme"]]]
         (map (fn [[k v]]
                [:tr
                 [:td [:span k]]
                 [:td (comp-select available k v)]])
              current))])

(defn theme-info []
  (let [theme (rf/subscribe [:css/theme])
        css-links (rf/subscribe [:css/app-theme-links])]
    [:div
     [:p.mt-5.mb-5.text-purple-600.text-3xl "components"]
     [show-theme @theme]
     [:p.mt-5.mb-5.text-purple-600.text-3xl "loaded css"]
     [show-css-links @css-links]]))

(defmethod reagent-page :goldly/theme [{:keys [route-params query-params handler] :as route}]
  [:div
   [header]
   [:div.container.mx-auto ; tailwind containers are not centered by default; mx-auto does this
    [theme-info]]])