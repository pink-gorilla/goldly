;; NOTEBOOK UI

(def show-viewer-debug-ui false) ; true for debugging
(def show-stacktrace false)

;; ervalerr

(defn stacktrace-line [idx {:keys [name file line class method
                                   ;type   flags ns fn
                                   ]}]
  (let [;tooling? (contains? flags :tooling)
        row-classes "" #_(str (name type) (when tooling? " tooling-stackframe"))]
    ^{:key idx}
    [:tr {:class row-classes}
     (case type
       :clj [:<>
             [:td [:span.text-blue-900 "ns"]]
             [:td [:span.text-blue-900 "fn"]]
             [:td [:span.text-blue-900 (str file ": " line)]]]
       :java [:<>
              [:td]
              [:td [:span.text-green-300 method]]
              [:td [:span.text-green-300 (str file ": " line)]]]
       [:<>
        [:td  [:span.text-red-300 method]]
        [:td [:span.text-red-300 method]]
        [:td [:span.text-red-300 (str file ": " line)]]])]))

(defn evalerr [{:keys [message class stacktrace] :as err}]
  [:div.text-red-500
   [:p class]
   [:p message]
   (when (and show-stacktrace stacktrace)
     [:table.w-full.text-md.bg-white.shadow-md.rounded.mb-4
      [:tbody
       (map-indexed stacktrace-line stacktrace)]])])

(pinkie/register-tag :p/evalerr evalerr)

;; segment

(defn segment-debug [segment]
  [:div.bg-gray-500
   [:p.font-bold "segment debug ui"]
   (pr-str segment)])

(defn segment [{:keys [src err out hiccup] :as segment}]
  (let [scode (:code segment)]
    [:div
     (when scode
       [user/highlightjs scode])
     (when err
       [evalerr err])
     (when (not (str/blank? out))
       [text2 out])
     (when hiccup
       [:div.mt-1.mb-1
        (->hiccup hiccup)])
     (when show-viewer-debug-ui
       [segment-debug segment])]))

;; notebook

(defn notebook-debug [nb]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "notebook debug ui"]
   (pr-str nb)])

(defn notebook [{:keys [meta content] :as nb}]
  (let [{:keys [ns eval-time]} meta]
    [:div.bg-indigo-50.p-2
     [:h1.text-xl.text-blue-800.text-xl.pb-2 ns]
     [:p.pb-2 "evaluated: " eval-time]
     [:hr.h-full.border-solid]
     (into [:div]
           (map segment content))
     (when show-viewer-debug-ui
       [notebook-debug nb])]))

(pinkie/register-tag :p/notebook notebook)

;; COLLECTION UI

(defn nb-item [fmt ns]
  [:p.w-full.truncate ; .overflow-x-hidden
   [link-dispatch [:bidi/goto :viewer :query-params {:ns ns :fmt (name fmt)}]
    (-> (str/split ns ".") last)
   ; ns
    ]])

(defn nb-list [[name [fmt list]]]
  (into
   [:div.w-full
    [:p.bg-red-300 name]
    (when show-viewer-debug-ui
      [:p (meta list) (pr-str list)])]
   (map #(nb-item fmt %) list)))

(defn notebook-collection [d]
  [:div.w-full.h-full.w-min-64
   (into
    [:div.flex.flex-col.items-stretch.bg-gray-50.h-full.w-full]
    (map #(nb-list %) d))])

(pinkie/register-tag :p/notebookcollection notebook-collection)

;; APP

(def nb-welcome
  {:meta {:ns "goldly.welcome"}
   :content
   [{:code "(println \"Welcome to Goldly Notebook Viewer \")"
     :hiccup [:h1.text-blue-800 "Welcome to Notebook Viewer!"]
     :out "Welcome to Goldly Notebook Viewer"}]})

(defn viewer-debug [query-params]
  [:div.bg-gray-500.pt-10.hoover-bg-blue-300
   [:p.font-bold "viewer debug ui"]
   [:p "query params:"]
   [:p (pr-str query-params)]
   [:p "ns: " (:ns query-params)]])

(defn viewer [query-params]
  (fn [query-params]
    [layout/sidebar-main
     [url-loader {:fmt :clj
                  :url :nb/collections}
      notebook-collection]
     [:div
      (if-let [ns (:ns query-params)]
        (let [fmt (or (:fmt query-params) :clj)
              fmt (if (string? fmt)
                    (keyword fmt)
                    fmt)]

          [url-loader #_{:fmt :edn
                         :url  (rdoc-link ns "notebook.edn")}
           {:fmt :clj
            :url :nb/load
             ;:arg-fetch ns
            :args-fetch [ns fmt]}
           notebook])
        [notebook nb-welcome])
      (when show-viewer-debug-ui
        [viewer-debug query-params])]]))

(defn viewer-page [{:keys [route-params query-params handler] :as route}]
  [:div.bg-green-300.w-screen.h-screen
   [viewer query-params]])

(add-page-template viewer-page :viewer)
