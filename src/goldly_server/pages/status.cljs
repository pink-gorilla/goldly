(ns goldly-server.pages.status
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]
   [bidi.bidi :as bidi]
   [webly.web.handler :refer [reagent-page]]
   [goldly.service.core :refer [run-a]]
   [ui.site.template :as t]
   [goldly-server.site :refer [header splash]]))

(defn link-fn [fun text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defn b [b]
  [:span.m-1 (key b)])

(defn bl [name l]
  [:div
   [:h2.text-xl.text-blue-700 name]
   [:p (map b l)]])

(defn mbl [ml]
  [:div
   (map (fn [[k v]]
          (bl k v)) ml)])

(defn m [mod]
  [:span.m-1 (:name mod)])
(defn modules [el]
  [:div
   [:h2.text-2xl.text-blue-700 "extensions"]
   [:p (map m el)]])

(defn status []
  (let [state (r/atom {})]
    (run-a state [:sci] :status/sci)
    (run-a state [:extensions] :status/extensions)
    (fn []
      (let [s (get-in @state [:sci :data])
            x (get-in @state [:extensions :data])]
        [:div
         [header]
         ;[:p (pr-str x)]
         [modules x]
         [:h2.text-2xl.text-blue-700 "bindings:"]
         [bl "user" (:bindings s)]
         [mbl (:ns-bindings s)]]))))

(defmethod reagent-page :goldly/status [{:keys [route-params query-params handler] :as route}]
  [status])