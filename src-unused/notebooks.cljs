(ns goldly-server.pages.notebooks
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [webly.web.handler :refer [reagent-page]]
   [ui.notebook.menu]
   [goldly.component.ui :refer [component]]
   [goldly-server.helper.site :refer [header]]
   [goldly-server.notebook.sidebar :refer [sidebar]]))


(defn notebooks-page []
  (let [notebook-names 8]
    (fn []
      ;[:div.bg-blue-200
      [:div.h-full.w-full.flex.flex-cols
        [sidebar]
       ;[:div @notebook-name]
       [:div
       [ui.notebook.menu/menu]
       [:div.w-full.h-full.bg-green-400
        [:div "nb"]
        
        ]]])))

(defmethod reagent-page :goldly/notebooks [{:keys [route-params query-params handler] :as route}]
  [:div.h-screen.w-screen
   [header]
   [notebooks-page]])

