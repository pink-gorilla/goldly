(ns goldly.devtools.page.runtime
  (:require
   [re-frame.core :as rf]
   [goldly.page :as page]
   ;[frisk :refer [frisk]]
   [goldly.devtools.url-loader :refer [url-loader]]
   [goldly.devtools.ui-helper :refer [add-page-template]]))

(defn kw-item [t]
  [:p.m-1 (pr-str t)])

(defn keyword-list [name list]
  [:div.mt-10
   [:h2.text-2xl.text-blue-700.bg-blue-300 name]
   (into [:div.grid.grid-cols-2.md:grid-cols-4]
         (map kw-item (sort list)))])

;[:h2.text-2xl.text-blue-700.bg-blue-300 "pinkie renderer - lazy"]
;(into [:p] (map p (sort (lazy/available))))

(defn ext [{:keys [name lazy]}]
  [:span.mr-2 name])

(defn extension-summary [exts]
  (into [:div
         [:h2.text-2xl.text-blue-700.bg-blue-300 "extensions"]
         ; (pr-str exts)
         ]
        (map ext exts)))

;;

(defn extension-details [exts]
  [:div
   [:h2.text-2xl.text-blue-700.bg-blue-300 "extension details"]
   (into [:div.ml-5
          ;(pr-str exts)
          ]
         []
        ;(map ext exts)
         )])

(defn config-info []
  (let [config (rf/subscribe [:webly/config])]
    (fn []
      [:div
       [:h2.text-2xl.text-blue-700.bg-blue-300 "config"]
        ;(pr-str @config)
       [:p "config is disabled until frisk lazy loading works"]
       ;[frisk @config]
       ])))

(defn run-sci-cljs-autoload [list]
  (let [data (:data list)]
    [:div.mt-10
     [:h2.text-2xl.text-blue-700.bg-blue-300 "sci-cljs auto-load"]
   ;[:div (pr-str data)]
     (into [:div.grid.grid-cols-2.md:grid-cols-4]
           (map (fn [s] [:p s]) (sort data)))]))

(defn runtime []
  [:div

   [config-info]

;[url-loader {:fmt :clj
;          :url 'goldly.config/info/extension-summary}
; extension-summary]

   ;[keyword-list "hiccup-fh (functional hiccup list) " (pinkie/tags)]
   [keyword-list "pages" (page/available)]

   [url-loader {:fmt :clj
                :url 'goldly.service.core/services-list}
    (partial keyword-list "services")]

   [url-loader {:fmt :clj
                :url 'goldly.run.services/run-sci-cljs-autoload}
    run-sci-cljs-autoload]])

(defn runtime-page [{:keys [route-params query-params handler] :as route}]
  [:div.bg-green-300
   [runtime]])

(add-page-template runtime-page :runtime)


