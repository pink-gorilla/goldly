(ns goldly.component.ui
  (:require
   [taoensso.timbre :as timbre :refer-macros [debugf infof errorf]]
   [cljs.pprint]
   [reagent.core :as r]
   [re-frame.core :as rf]))

(defmulti render-component (fn [m] (:type m)))

(defmethod render-component :default [{:keys [id type]}]
  [:div (str "unknown comp type: " type)])

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

(defn component-loading [type id]
  [:div.bg-orange-300.m-16
   [:h1 (str "loading " type)]
   [:p id]])

(defn component-nil [type id]
  [:div.bg-red-500.m-16
   [:h1 (str type " [" id "] does not exist!")]
   [:p id]])

(defn component-shower [comp-type comp-id args data]
  (cond
    (nil? @data) [component-loading comp-id]
    (= :g/system-nil (:status @data))  [component-nil comp-id]
    :else
    [:<>
     ;[:p "type: " comp-type " id:" comp-id]
     ;[:p "args: " args]
     ;[:p "component: " (pr-str @data)]
     [error-boundary
      [render-component {:type comp-type
                         :id comp-id
                         :data data
                         :args args}]]]))

(defn component
  "requests system with id from server and displays it."
  [comp-type comp-id args]
  (let [id-kw (if (string? comp-id) (keyword comp-id) comp-id)
        data (rf/subscribe [:component/show comp-type id-kw])]
    (debugf "loading component type: %s id: %s" comp-type comp-id)
    (rf/dispatch [:ws/send [:component/load {:type comp-type
                                             :id id-kw}]])
    [component-shower comp-type id-kw args data]))

