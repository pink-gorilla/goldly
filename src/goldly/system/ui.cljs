(ns goldly.system.ui
  (:require
   [cljs.pprint]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [goldly.system.sci :refer [render-system]]))

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
   [:h1 (str "system [" id "] does not exist!")]
   [:p id]])

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
        system (rf/subscribe [:goldly/system id-kw])]
    (rf/dispatch [:goldly/send :goldly/system id-kw])
    [system-shower id ext system]))

(defn system [id]
  [system-ext id ""])
