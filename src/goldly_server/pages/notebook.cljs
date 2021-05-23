(ns goldly-server.pages.notebook
  (:require
   [re-frame.core :as rf]
   [webly.web.handler :refer [reagent-page]]

   [picasso.default-config] ; side-effects
   [picasso.data.document :as data]
   [picasso.kernel.view.picasso]
   [picasso.kernel.view.default-painter] ; side-effects
   [picasso.document.transactor] ;side-effects
   [picasso.document.position] ;side-effects
   ; ui
   [ui.markdown.goldly.core] ;side-effects
   [ui.markdown.viewer]
   [ui.code.goldly.core] ;side-effects
   [ui.notebook.core :refer [notebook-view]]))

(rf/dispatch [:doc/add data/document])
(rf/dispatch [:doc/doc-active (:id data/document)])

(def opts
  {; if a layout option is passed this will override the settings in localstorage
   ;:layout :single ; :vertical ; :horizontal
   :view-only true})

(defmethod reagent-page :notebook/current [{:keys [route-params query-params handler] :as route}]
  [notebook-view opts])