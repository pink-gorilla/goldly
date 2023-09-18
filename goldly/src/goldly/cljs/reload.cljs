(ns goldly.cljs.reload
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :refer-macros [trace debug debugf info infof warn warnf error errorf]]
   ;[reagent.impl.batching :refer [flush]] ; flushing does not help.
   [frontend.routes :refer [current]]
   [frontend.page :refer [add-page]]
   [frontend.page.viewer :refer [refresh-page]]))

(defn reloading-cljs-ui [_route]
  [:div "reloading cljs (sci) code"])

(add-page :goldly/reload-cljs reloading-cljs-ui)

(defn reload-cljs []
  (let [c @current]
    (warn "reload: " c)
    (rf/dispatch [:bidi/goto :goldly/reload-cljs])
    (rf/dispatch [:bidi/goto-route c])
    ;(flush) ; does not work
    (refresh-page)))