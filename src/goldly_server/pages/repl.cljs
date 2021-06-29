(ns goldly-server.pages.repl
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [webly.web.handler :refer [reagent-page]]
   [picasso.default-config] ; side-effects
   [goldly.data.notebook :as data]
   [ui.notebook.core :refer [notebook-view]]
   [ui.notebook.menu]))

; here for debugging of cljs kernel 

(def opts
  {; if a layout option is passed this will override the settings in localstorage
   :layout  :stacked ; :vertical ; :horizontal :single ;
   :view-only false})

(rf/dispatch [:css/set-theme-component :codemirror "mdn-like"])

(defn link-fn [fun text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defn menu []
  [:div
   ;[link-href "/" "main"]
   ;[link-dispatch [:bidi/goto :goldly/about] "main"]
   [link-dispatch [:bidi/goto "/"] "main"]
   [ui.notebook.menu/menu]])

(defmethod reagent-page :goldly/repl [{:keys [route-params query-params handler] :as route}]
  (let [first (r/atom true)]
    (fn [{:keys [route-params query-params handler] :as route}]
      (when @first
        (reset! first false)
        (rf/dispatch [:doc/load data/notebook]))
      [:div
       [menu]
       [notebook-view opts]])))