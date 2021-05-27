(ns goldly-server.pages.notebook
  (:require
   [re-frame.core :as rf]
   [webly.web.handler :refer [reagent-page]]
   [picasso.default-config] ; side-effects
   [picasso.data.document :as data]
   ;[picasso.kernel.view.picasso]
   ;[picasso.kernel.view.default-painter] ; side-effects
   ;[picasso.document.transactor] ;side-effects
   ;[picasso.document.position] ;side-effects
   ; ui
   ;[ui.markdown.goldly.core] ;side-effects
   ;[ui.markdown.viewer]
   ;[ui.code.goldly.core] ;side-effects
   [ui.notebook.core :refer [notebook-view]]
   [ui.notebook.menu]))

(rf/dispatch [:doc/load data/document])

(def opts
  {; if a layout option is passed this will override the settings in localstorage
   ;:layout :single ; :vertical ; :horizontal
   :view-only true})

(defmethod reagent-page :notebook/current [{:keys [route-params query-params handler] :as route}]
  [:div
   [ui.notebook.menu/menu]
   [notebook-view opts]])