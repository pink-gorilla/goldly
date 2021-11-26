;; comes from pinkie
;; but we need more customization!


(defn line-with-br [t]
  [:div
   [:span.font-mono.whitespace-pre t]
   [:br]])

(defn text2
  "Render text (as string) to html
   works with \\n (newlines)
   Needed because \\n is meaningless in html"
  ([t]
   (text2 {} t))
  ([opts t]
   (let [lines (str/split t #"\n")]
     (into
      [:div (merge {:class "textbox text-lg"} opts)]
      (map line-with-br lines)))))

;; block

(defn block [& children]
  (into [:div.bg-blue-400.m-5.inline-block {:class "w-1/4"}]
        children))

;; grid of cols

(defn s-cols [nr]
  (->> (take nr (repeatedly (fn [] "1fr ")))
       (str/join "")))

(defn grid [{:keys [cols background-color]
             :or {cols 2
                  background-color "orange"}} & children]
  (into ^:R [:div {:style {:display :grid
                           :grid-template-columns  (s-cols cols) ; "400px 400px 400px 400px" 
                           :background-color background-color}}]
        children))

;; rdocs

(defn rdoc-link [ns name]
  (str "/api/rdocument/file/" ns "/" name))

;; links

(defn link-fn [fun text]
  [:a.bg-blue-600.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-600.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

;; site layout

(defn devtools-header []
  [site/header-menu
   {:brand "Application"
    :brand-link "/"
    :items [{:text "notebooks" :dispatch [:bidi/goto :viewer :query-params {}]} ;  :link "/devtools/viewer"
            {:text "scratchpad"  :dispatch [:bidi/goto :scratchpad]}  ; :link "/devtools/scratchpad"
            {:text "theme"  :dispatch [:bidi/goto :theme]} ;  :link "/devtools/theme"
            {:text "environment"  :dispatch  [:bidi/goto :environment]} ; :link "/devtools/environment"
            {:text "pages"  :dispatch [:bidi/goto :pages]}  ; :link "/devtools/pages"
            {:text "help"  :dispatch [:bidi/goto :devtools]}  ; :link "/devtools"
            {:text "feedback" :link "https://github.com/pink-gorilla/goldly/issues" :special? true}]}])

(defn add-page-template [page name]
  (let [wrapped-page (fn [route]
                       [layout/header-main  ; .w-screen.h-screen
                        [devtools-header]
                        [page route]])]
    (add-page wrapped-page name)))

;; styling

(defn h1 [t]
  [:h1.text-xl.text-red-900.mt-5 t])

(rf/dispatch [:css/set-theme-component :tailwind-full "light"])
(rf/dispatch [:css/set-theme-component :tailwind-girouette false])