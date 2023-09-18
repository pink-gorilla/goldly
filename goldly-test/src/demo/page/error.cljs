(ns demo.page.error
  (:require
   [demo.cljs-libs.helper :refer [wrap-layout]]))

(defn exception-component
  "a component that throws exceptions for testing."
  []
  (throw {:type :custom-error
          :message "Something unpleasant occurred"}))

(defn error-page [_r]
  [:div
   [exception-component]])

(def error-page
  (wrap-layout error-page))

