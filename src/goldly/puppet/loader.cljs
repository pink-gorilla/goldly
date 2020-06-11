(ns goldly.puppet.loader
  (:require-macros [secretary.core :refer [defroute]])
  (:import
   [goog History]
    ;; [goog.history Html5History]
   )
  (:require
   [cljs.pprint]
   [taoensso.timbre :as timbre :refer-macros (tracef debugf infof warnf errorf info)]
   [goog.events :as events]
   [goog.history.EventType :as EventType]
   [secretary.core :as secretary]
   [reagent.core :as r]
   [reagent.dom]
   [re-frame.core :refer [dispatch dispatch-sync clear-subscription-cache! subscribe]]

   ; the following just adds dependencies to bundle
   [pinkgorilla.ui.default-setup] ; renderable-ui
   [pinkgorilla.ui.default-renderer] ; gorilla-ui 
   [pinkgorilla.ui.gorilla-plot.pinkie] ; gorilla-plot
   ;[pinkgorilla.ui.pinkie :refer [tag-inject renderer-list]]

   [goldly.web.ws :refer [send! start-router!]]
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

(defn system
  "requests system with id from server
   and displays it."
  [id]
  (info "showing system: " id)
  (dispatch-sync [:goldly/system-store id nil])
  (dispatch [:goldly/send :goldly/system id])
  (let [system (subscribe [:system])]
    (fn []
      [:<>
       [:a {:class "m-2 bg-blue-200 border-dotted border-orange-400"
            :href "#/info"} "Systems"]
       (if (nil? @system)
         [:h1 "loading .."]
         [:<>
          [:h1.bg-orange-300 (str (:name @system) " " id)]
          [error-boundary
           [render-system (merge {:id (:id @system)}
                                 (:cljs @system)
                                 {:fns-clj (:fns-clj @system)})]]])])))