(ns demo.cljs-libs.helper
  (:require
   [site]
   [layout]))

;; clj-libs is here, because 
;; it shows that we can add custom libraries inside sci source code files.

(defn add-3 [a]
  (+ a 3))

(defn test-header []
  [site/header-menu
   {:brand "goldly-test"
    :brand-link "/"
    :items [{:text "service-test" :dispatch [:bidi/goto 'demo.page.service-test/service-page]}
            {:text "sci-compile"  :dispatch [:bidi/goto 'demo.page.sci-compile/sci-compile-page]}
            {:text "lazyload-test"  :dispatch  [:bidi/goto 'demo.page.lazy/lazy-page]}
            {:text "select"  :dispatch  [:bidi/goto 'demo.page.select/select-page]}
            {:text "error-test"  :dispatch [:bidi/goto 'demo.page.error/error-page]}
            {:text "tick"  :dispatch [:bidi/goto 'demo.page.tick/tick-page]}]}])

(defn wrap-layout [page]
  (fn [route]
    [layout/header-main  ; .w-screen.h-screen
     [test-header]
     [page route]]))