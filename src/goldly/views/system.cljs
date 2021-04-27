(ns goldly.views.system
  (:require
   [cljs.pprint]
   [taoensso.timbre :as timbre :refer-macros [info]]
   [reagent.core :as r]
   [re-frame.core :refer [dispatch dispatch-sync subscribe]]
   [webly.web.handler :refer [reagent-page]]
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

(defn systems-menu []
  [:a.pr-2.text-right.text-blue-600.text-bold.tracking-wide.font-bold.border.border-blue-300.rounded.cursor-pointer
   {:on-click #(dispatch [:bidi/goto :goldly/system-list])
    :style {:position "absolute"
            :z-index 200 ; dialog is 1040 (we have to be lower)
            :top "10px"
            :right "10px"
            :width "80px"
            :height "30px"}} "Systems"])

(defn systems-header [system id]
  [:h1.bg-orange-300 (str (:name @system) " " id)])

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
       [systems-menu]
       #_[:a {:class "m-2 bg-blue-200 border-dotted border-orange-400"
              :on-click #(dispatch [:bidi/goto :ui/system-list])} "Systems"] ; "#/info"
       (case @system
         :g/system-nil [system-nil id]
         :g/system-loading [system-loading id]
         (if (nil? @system)
           [:h1 "something is broken (the system is nil)"]
           [:<>
            ;[systems-header system id]
            [error-boundary
             [render-system (merge {:id (:id @system)}
                                   (:cljs @system)
                                   {:fns-clj (:fns-clj @system)})]]]))])))

(defmethod reagent-page :ui/system [{:keys [route-params handler]}]
  (info "loading system" route-params)
  [system (:system-id route-params)])