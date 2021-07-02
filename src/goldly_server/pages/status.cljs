(ns goldly-server.pages.status
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]
   [bidi.bidi :as bidi]
   [webly.web.handler :refer [reagent-page]]
   [pinkie.pinkie]
   [goldly.service.core :refer [run-a]]
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
  [:div.mt-10
   [:h2.text-2xl.text-blue-700 "extensions"]
   [:p (map m el)]])

(defn p [t]
  [:span.m-1 (pr-str t)])
(defn pinkie []
  [:div.mt-10
   [:h2.text-2xl.text-blue-700 "pinkie renderer"]
   [:p (map p (keys @pinkie.pinkie/component-registry))]])

(defn services [ss]
  [:div.mt-10
   [:h2.text-2xl.text-blue-700 "services"]
   [:p (map p ss)]])

(defn status []
  (let [first (r/atom true)
        state (r/atom {})]
    (when @first
      (reset! first false)
      (run-a state [:sci] :status/sci)
      (run-a state [:extensions] :status/extensions)
      (run-a state [:version] :goldly/version "goldly")
      (run-a state [:services] :goldly/services))
    (fn []
      (let [s (get-in @state [:sci :data])
            x (get-in @state [:extensions :data])
            v (get @state :version)
            ss (get @state :services)]
        [:div
         [header]
         ;[:p (pr-str x)]
         [:h1.text-2xl.text-blue-700 "goldly"]
         (when v
           [:div (str "goldly " (:version v) " generated: " (:generated-at v))])
         (when ss
           [services ss])
         [modules x]
         [:h2.text-2xl.text-blue-700.mt-10 "bindings"]
         [bl "user" (:bindings s)]
         [mbl (:ns-bindings s)]

         [pinkie]]))))

(defmethod reagent-page :goldly/status [{:keys [route-params query-params handler] :as route}]
  [status])