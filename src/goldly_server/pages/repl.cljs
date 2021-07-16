(ns goldly-server.pages.repl
  (:require
   [re-frame.core :as rf]
   [webly.web.handler :refer [reagent-page]]
   [picasso.default-config] ; side-effects
   [ui.notebook.menu]
   [ui.notebook.loader.page :refer [page-notebook]]
   [goldly-server.helper.ui :refer [link-dispatch link-href]]
   [ui.notebook.loader.list] ; side effects
   ))

(rf/dispatch [:css/set-theme-component :codemirror "mdn-like"])

(defn menu []
  [:div
   [link-href "/" "user app main"]
   ;[link-dispatch [:bidi/goto :goldly/about] "main map"]
   ;[link-dispatch [:bidi/goto "/"] "main"]
   [ui.notebook.menu/menu]])

(defmethod reagent-page :goldly/repl [{:keys [route-params query-params handler] :as route}]
  ; since repl fn is a level 2 cmponent, we would have to return a multi method.
  ; I dont know if reagent supports level2 multi methods.
  ; so repl gets moved to a normal fn.
  [page-notebook menu])

(rf/reg-event-fx
 :document/new
 (fn [_ [_]]
   (rf/dispatch [:doc/new])))