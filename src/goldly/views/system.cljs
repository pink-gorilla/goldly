(ns goldly.views.system
  (:require
   [cljs.pprint]
   [taoensso.timbre :as timbre :refer-macros [info]]
   [reagent.core :as r]
   [re-frame.core :refer [dispatch dispatch-sync subscribe]]
   [webly.web.handler :refer [reagent-page]]
   [goldly.sci.system :refer [render-system]]
   [goldly.views.site :refer [header]]))

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

(defn systems-header [system id]
  [:h1.bg-orange-300 (str (:name @system) " " id)])

(defn system-shower [id ext system]
  (cond
    (nil? @system) [system-loading id]
    (= :g/system-nil (:status @system))  [system-nil id]
    :else
    [:<>
     ;[:p "id:" id]
     ;[:p "system" (pr-str @system)]
     [error-boundary
      [render-system (merge {:id (:id @system)
                             :fns-clj (:fns-clj @system)}
                            (:cljs @system)) ext]]]))

(defn system-ext
  "requests system with id from server and displays it."
  [id ext]
  (let [id-kw (if (string? id) (keyword id) id)
        system (subscribe [:goldly/system id-kw])]
    (dispatch [:goldly/send :goldly/system id-kw])
    [system-shower id ext system]))

(defn system [id]
  [system-ext id ""])

(defn system-themed [id ext]
  [:div
     ;[systems-menu]
     ; #[:a {:class "m-2 bg-blue-200 border-dotted border-orange-400"
     ;        :on-click #(dispatch [:bidi/goto :ui/system-list])} "Systems"] ; "#/info"
   [header]
   [:div.container.mx-auto ; tailwind containers are not ventered by default; mx-auto does this
    [:p.mt-5.mb-5.text-purple-600.text-3xl id]
    [system-ext id ext]]])

(defmethod reagent-page :goldly/system [{:keys [route-params query-params handler] :as route}]
  (info "loading system" route-params)
  [system-themed (:system-id route-params) ""])

(defmethod reagent-page :goldly/system-ext [{:keys [route-params query-params handler] :as route}]
  (info "loading system-ext" route-params)
  [system-themed (:system-id route-params) (:system-ext route-params)])