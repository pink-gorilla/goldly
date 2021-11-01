(ns goldly-server.pages.status
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]
   [bidi.bidi :as bidi]
   [taoensso.timbre :refer-macros [trace debug debugf info infof warn warnf error errorf]]

   [webly.web.handler :refer [reagent-page]]
   [webly.build.lazy :as lazy]
   [pinkie.pinkie]
   [goldly.service.core :refer [run-a]]
   [goldly.extension.lazy :refer [add-load-status]]
   [goldly-server.helper.site :refer [header splash]]
   [goldly-server.helper.ui :refer [link-dispatch link-href]]
   [goldly-bindings-generated]))

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

;; extensions

(defn check [b]
  (if b
    [:i.far.fa-check-circle]
    [:i.fas.fa-times-circle]))

(defn ext [{:keys [name lazy loaded]}]
  [:tr
   [:td name]
   [:td (check lazy)]
   [:td (check loaded)]])

(defn extensions [ext-seq]
  (let [ext-seq-with-load (add-load-status ext-seq)]
    [:div.mt-10
     [:h2.text-2xl.text-blue-700.bg-blue-300 "extensions"]
     [:table
      (into [:tbody
             [:tr
              [:td "name"]
              [:td "lazy"]
              [:td "loaded"]]] (map ext ext-seq-with-load))]]))

;; pinkie
(defn p [t]
  [:span.m-1 (pr-str t)])
(defn pinkie []
  [:div.mt-10
   [:h2.text-2xl.text-blue-700.bg-blue-300 "pinkie renderer"]
   (into [:p] (map p (sort (keys @pinkie.pinkie/component-registry))))
   [:h2.text-2xl.text-blue-700.bg-blue-300 "pinkie renderer - lazy"]
   (into [:p] (map p (sort (lazy/available))))])

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
      ;(run-a state [:extensions] :extension/all)
      (run-a state [:extensions] :extension/summary)
      (run-a state [:version] :goldly/version "goldly")
      (run-a state [:services] :goldly/services))
    (fn []
      (let [s (get-in @state [:sci :data])
            x (get-in @state [:extensions])
            v (get @state :version)
            ss (get @state :services)]
        [:div
         [header]
         ;[:p (pr-str x)]
         [:h1.text-2xl.text-blue-700 "goldly"]
         (when v
           [:div (str "goldly version:" (:version v)
                      " generated: " (:generated-at v)
                      "binding create time: " goldly-bindings-generated/compile-time)])

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