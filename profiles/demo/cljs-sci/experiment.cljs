
; test !!
9999


(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defmethod reagent-page :user/experiment [{:keys [route-params query-params handler] :as route}]
  [:div
   [link-href "/" "main"]
   [:div.text-green-300 "experiments..."]
   [:p "add code here..."]
   [:p "adding" (+ 7 7)]
   ])
