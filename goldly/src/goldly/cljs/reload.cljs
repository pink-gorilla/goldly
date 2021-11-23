(ns goldly.cljs.reload
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :refer-macros [trace debug debugf info infof warn warnf error errorf]]
   ;[reagent.impl.batching :refer [flush]] ; flushing does not help.
   [bidi.bidi :as bidi]
   [frontend.routes :refer [current]]
   [frontend.page :refer [reagent-page]]
   [webly.app.views :refer [refresh-page]]))

(defn reloading-cljs-ui []
  [:div "reloading cljs (sci) code"])

(defmethod reagent-page :goldly/reload-cljs [{:keys [route-params query-params handler] :as route}]
  [reloading-cljs-ui])

(defn reload-cljs []
  (let [c @current]
    (warn "reload: " c)
    (rf/dispatch [:bidi/goto :goldly/reload-cljs])
    (rf/dispatch [:bidi/goto-route c])
    ;(flush) ; does not work
    (refresh-page)))