(ns goldly.puppet.loader
  (:require
   [cljs.pprint]
   [taoensso.timbre :as timbre :refer-macros [info]]
   [reagent.core :as r]
   [re-frame.core :refer [dispatch dispatch-sync subscribe]]
   [bidi.bidi :as bidi]
   [webly.web.handler :refer [reagent-page]]
   [goldly.web.routes :refer [goldly-routes-frontend]]
   [goldly.system :refer [render-system]]))

(defn error-boundary [_ #_comp]
  (let [error (r/atom nil)
        info (r/atom nil)]
    (r/create-class
     {:component-did-catch (fn [_ #_this _ #_e i]
                             (reset! info i))
      :get-derived-state-from-error (fn [e]
                                      (reset! error e)
                                      #js {})
      :reagent-render (fn [comp]
                        (if @error
                          [:div "Something went wrong."
                           [:p (str @error)]]
                          comp))})))

(defn system-loading [id]
  [:div.bg-orange-300.m-16
   [:h1 "loading system "]
   [:p id]])

(defn system-nil [id]
  [:div.bg-red-500.m-16
   [:h1 "system does not exist!"]
   [:p id]])

(defn system
  "requests system with id from server
   and displays it."
  [id]
  (info "showing system: " id)
  (dispatch-sync [:goldly/system-store :g/system-loading])
  (dispatch [:goldly/send :goldly/system id])
  (let [system (subscribe [:system])]
    (fn []
      [:<>
       [:a {:class "m-2 bg-blue-200 border-dotted border-orange-400"
            :href (bidi/path-for goldly-routes-frontend :ui/main)} "Systems"] ; "#/info"
       (case @system
         :g/system-nil [system-nil id]
         :g/system-loading [system-loading id]
         (if (nil? @system)
           [:h1 "something is broken"]
           [:<>
            [:h1.bg-orange-300 (str (:name @system) " " id)]
            [error-boundary
             [render-system (merge {:id (:id @system)}
                                   (:cljs @system)
                                   {:fns-clj (:fns-clj @system)})]]]))])))

(defmethod reagent-page :ui/system [{:keys [route-params handler]}]
  (info "loading system" route-params)
  [system (:system-id route-params)])