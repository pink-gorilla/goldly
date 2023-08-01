(ns demo.page.error
  (:require
   [demo.cljs-libs.helper :refer [add-page-test]]))

(defn exception-component
  "a component that throws exceptions for testing."
  []
  (throw {:type :custom-error
          :message "Something unpleasant occurred"}))

(defn error-page [_r]
  [:div
   [exception-component]])

(add-page-test error-page :user/error)
