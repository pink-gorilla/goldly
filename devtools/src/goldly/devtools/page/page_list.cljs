(ns goldly.devtools.page.page-list
  (:require
   [goldly.page :as page]
   [goldly.devtools.ui-helper :refer [devtools-header link-dispatch]]))

(defn page-item [i]
  [:span.m-1
   [link-dispatch [:bidi/goto :pages :query-params {:page (name i)
                                                    :ns (namespace i)}]
    (str i)]])

(defn page-list [p]
  (into
   [:div.grid.grid-cols-1.md:grid-cols-2
    ;[:h1 "page list"]
    ]
   (map page-item p)))

(defn page-show [page route]
  (if page
    (let [r (assoc route :handler page)]
      [:div ; .mt-6
       ;[:span.text-xl.text-blue-500.text-bold.mr-4 (str page)]
       ;[:p (pr-str r)]
       (page/show r)])
    [:div.mt-6
     [:span.text-xl.text-blue-500.text-bold.mr-4 "page: " "please select a page"]]))

(defn get-available-pages []
  (->>  (page/available)
        (remove #(= :pages %))
        (remove #(= :goldly/reload-cljs %))))

(defn blank? [s]
  (or (= s "")
      (= s nil)))

(defn page-list-page [{:keys [route-params query-params handler] :as route}]
  (let [p (get-available-pages)]
    (fn [{:keys [route-params query-params handler] :as route}]
      (let [ns (:ns query-params)
            page (:page query-params)
            page (if (string? page)
                   (if (blank? ns)
                     (keyword page)
                     (keyword ns page))
                   page)]
  ;[:div.bg-green-300.w-screen.h-screen.overflow-scroll
        [:div
         ;[:span.text-xl.text-blue-500.text-bold.mr-4 "pages"]
         (if page
           [page-show page route]
           [:div
            [devtools-header]
            [page-list p]])]))))

(page/add page-list-page :pages)