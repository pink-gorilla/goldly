(ns goldly-server.pages.notebook
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [webly.web.handler :refer [reagent-page]]
   [picasso.default-config] ; side-effects
   [picasso.data.notebook :as data]
   [ui.notebook.core :refer [notebook-view]]
   [ui.notebook.menu]))

; here for debugging of cljs kernel 

(def opts
  {; if a layout option is passed this will override the settings in localstorage
   ;:layout :single ; :vertical ; :horizontal
   :view-only true})

(defmethod reagent-page :notebook/test [{:keys [route-params query-params handler] :as route}]
  (let [first (r/atom true)])
  (fn [{:keys [route-params query-params handler] :as route}]
    (when @first
      (reset! first false)
      (rf/dispatch [:doc/load data/notebook]))
    [:div
     [ui.notebook.menu/menu]
     [notebook-view opts]]))