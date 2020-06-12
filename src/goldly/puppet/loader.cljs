(ns goldly.puppet.loader
  (:import
   [goog History]
    ;; [goog.history Html5History]
   )
  (:require
   [cljs.pprint]
   [taoensso.timbre :as timbre :refer-macros (tracef debugf infof warnf errorf info)]
   [reagent.core :as r]
   [re-frame.core :refer [dispatch dispatch-sync subscribe]]
   [bidi.bidi :as bidi]
   [goldly.web.routes :refer [app-routes]]
   [goldly.system :refer [render-system]]
   [goldly.events] ; add reframe event handlers
   [goldly.puppet.subs]))

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
            :href (bidi/path-for app-routes :main)} "Systems"] ; "#/info"
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