(def cookie-state (r/atom nil))
(defn fortune []
  [:div
    [:h1 "Fortune Cookies (Snippet)"]
    [:p "a demonstration to call server-side clj-functions (clj based)."]
           [:p "This would work with read database queries also :-)"]
           [:button {:class "border m-2 p-3 border-pink-500"
                     :on-click (fn [& _]
                                 (println "getting cookie")
                                 (run-a cookie-state [:cookie] :cookie/get 3))} "get a specific cookie"]
           [:button {:class "border m-2 p-3 border-pink-500"
                     :on-click (fn [& _]
                                 (println "getting cookie")
                                 (run-a cookie-state [:cookie] :cookie/get))} "get a random cookie"]
           [:p.bg-yellow-500.italic.text-xl.text-blue-700
            (or (:cookie @cookie-state) "no cookie received!")]])


(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defmethod reagent-page :user/fortune [{:keys [route-params query-params handler] :as route}]
  [:div
   [link-href "/" "main"]
   [:div.text-green-300 "fortune cookies"]
   [fortune]])
