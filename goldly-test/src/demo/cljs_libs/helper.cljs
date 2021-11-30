
;; clj-libs is here, because 
;; it shows that we can add custom libraries inside sci source code files.

(defn add-3 [a]
  (+ a 3))

(defn test-header []
  [site/header-menu
   {:brand "goldly-test"
    :brand-link "/"
    :items [{:text "service-test" :dispatch [:bidi/goto :user/service]}
            {:text "sci-compile"  :dispatch [:bidi/goto :user/scicompile]}
            {:text "lazyload-test"  :dispatch  [:bidi/goto  :user/lazy]}
            {:text "select"  :dispatch  [:bidi/goto  :user/select]}
            {:text "error-test"  :dispatch [:bidi/goto :user/error]}]}])

(defn add-page-test [page name]
  (let [wrapped-page (fn [route]
                       [layout/header-main  ; .w-screen.h-screen
                        [test-header]
                        [page route]])]
    (add-page wrapped-page name)))

