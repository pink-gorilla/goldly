(ns demo.cljs-libs.helper
  (:require
   [ui.site.template :as site]
   [ui.site.layout :as layout]))

(defn test-header []
  [site/header-menu
   {:brand "goldly-demo"
    :brand-link "/"
    :items [{:text "sci-compile"  :dispatch [:bidi/goto 'demo.page.sci-compile/sci-compile-page]}
            {:text "lazyload-test"  :dispatch  [:bidi/goto 'demo.page.lazy/lazy-page]}
            {:text "error-test"  :dispatch [:bidi/goto 'demo.page.error/error-page]}
            {:text "sci-interpreted"  :dispatch [:bidi/goto 'demo.page.sci-page-interpreted/sci-page]}]}])

(defn wrap-layout [page]
  (fn [route]
    [layout/header-main
     [test-header]
     [page route]]))