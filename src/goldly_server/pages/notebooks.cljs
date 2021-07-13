(ns goldly-server.pages.notebooks
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [webly.web.handler :refer [reagent-page]]
   [goldly.component.ui :refer [component]]
   [goldly-server.helper.site :refer [header]]))

(defn notebooks-list [set-notebook notebook-name {:keys [notebooks]}]
  (let [notebooks (or notebooks [])]
    (into [:ul.flex.flex-col.h-full.w-32.bg-red-200]
          (for [name notebooks]
            ^{:key name}
            [:li.m-5
             [:a {:class (str "m-1 p-1 border-round border-1 border-purple-500 shadow "
                              "hover:border-gray-500 "
                              (if (= notebook-name name)
                                "bg-blue-300"
                                "bg-yellow-300"))
                  :on-click  #(set-notebook name)}
              name]]))
      ;[:div (pr-str notebooks)]
    ))

(defn notebooks-page []
  (let [routes (rf/subscribe [:webly/routes])
        notebook-names (rf/subscribe [:index/show :notebook])
        first (r/atom true)
        notebook-name (r/atom nil)
        set-notebook (fn [n] (reset! notebook-name n))]
    (fn []
      ;[:div.bg-blue-200
      (when @first
        (reset! first false)
        ;(request-systems)
        nil)
      [:div.h-full.w-full.flex.flex-cols
       [notebooks-list set-notebook @notebook-name @notebook-names]
       ;[:div @notebook-name]
       [:div.w-full.h-full.bg-green-400
        (when @notebook-name
          [component :notebook @notebook-name {}])]])))

(defmethod reagent-page :goldly/notebooks [{:keys [route-params query-params handler] :as route}]
  [:div.h-screen.w-screen
   [header]
   [notebooks-page]])




