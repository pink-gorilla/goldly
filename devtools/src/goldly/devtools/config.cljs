

(defn config-info []
  (let [config (rf/subscribe [:webly/config])]
    (fn []
      [:div
       [:p.mt-5.mb-5.text-purple-600.text-3xl "config"]
        (pr-str @config)]
       )))


(defn config-page [{:keys [route-params query-params handler] :as route}]
  [:div.container.mx-auto ; tailwind containers are not centered by default; mx-auto does this
   [:span.text-xl.text-blue-500.text-bold.mr-4 "config"]
   [config-info]
   ]
  
  )

(add-page-template config-page :config)
