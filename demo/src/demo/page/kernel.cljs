(ns demo.page.kernel
  (:require
   [reval.kernel.protocol :refer [kernel-eval available-kernels]]
   [demo.cljs-libs.helper :refer [wrap-layout]]))

(defn kernel-page1 [_]
  [:div
   [:p "kernels:" (pr-str (available-kernels))]])

(def kernel-page
  (wrap-layout kernel-page1))