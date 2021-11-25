



(defn build-page [{:keys [route-params query-params handler] :as route}]
  [:div.container.mx-auto ; tailwind containers are not centered by default; mx-auto does this
   [:span.text-xl.text-blue-500.text-bold.mr-4 "build"]
 ;  [build-info]
   ]
  
  )

(add-page-template build-page :build)
