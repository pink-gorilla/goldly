(ns goldly-server.pages.status
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]
   [bidi.bidi :as bidi]
   [webly.web.handler :refer [reagent-page]]
   [pinkie.pinkie]
   [goldly.service.core :refer [run-a]]
   [goldly-server.site :refer [header splash]]
   [goldly-server.helper.ui :refer [link-dispatch link-href]]))

(defn b [b]
  [:span.m-1 (key b)])

(defn bl [name l]
  [:div
   [:h2.text-xl.text-blue-700 name]
   (into [:p] (map b l))])

(defn mbl [ml]
  (into [:div]
        (map (fn [[k v]]
               (bl k v)) ml)))

(defn m [mod-name]
  [:span.m-1 mod-name])
(defn extensions [modules]
  (let [names (-> (map :name modules)
                  (sort))]
    [:div.mt-10
     [:h2.text-2xl.text-blue-700.bg-blue-300 "extensions"]
     (into [:p] (map m names))]))

(defn p [t]
  [:span.m-1 (pr-str t)])
(defn pinkie []
  [:div.mt-10
   [:h2.text-2xl.text-blue-700.bg-blue-300 "pinkie renderer"]
   (into [:p] (map p (sort (keys @pinkie.pinkie/component-registry))))])

(defn services [ss]
  [:div.mt-10
   [:h2.text-2xl.text-blue-700.bg-blue-300 "services"]
   (into [:p] (sort (map p ss)))])

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

         ;[:p (pr-str x)]
         [extensions x]
         [:h2.text-2xl.text-blue-700.bg-blue-300.mt-10 "bindings"]
         [bl "user" (:bindings s)]
         [mbl (:ns-bindings s)]

         [pinkie]
         (when ss
           [services ss])]))))

(defmethod reagent-page :goldly/status [{:keys [route-params query-params handler] :as route}]
  [status])