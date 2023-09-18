(ns goldly.page.no-page
  (:require
   [frontend.page :refer [add-page]]
   [re-frame.core :as rf]))

(defn no-page [_route]
  [:div.m-10.p-10.bg-blue-300.border.border-round.border-red-600
   [:p.text-2xl.text-red-800.mb-10 "Here could be your app!"]
   [:p "Your config needs to include [:goldly :routes] {:app {\"/\" :your-page } :api {} }"]
   [:p.m-2.border.bg-blue-500.w-32
    [:a {:on-click #(rf/dispatch [:bidi/goto :devtools])}
     "Goto Devtools"]]])

(add-page :goldly/no-page no-page)
