(ns goldly.template
  (:require
   [reagent.core :as r]))

;; helper

(defn into-text [el t]
  (if (string? t)
    (into el t)
    (into el t)
    ;el
    ))

;; header

(defn menu-item [{:keys [text link special?]}]
  (if special?
    [:a {:class "px-4 py-1 text-sm font-medium text-center text-gray-200 transition-colors duration-300 transform border rounded hover:bg-indigo-400"
         :href link} text]
    [:a {:class "text-sm font-medium text-gray-200 transition-colors duration-300 transform hover:text-indigo-400"
         :href link} text]))

(defn comp-nav [{:keys [brand brand-link items]}]
  (let [open? (r/atom false)]
    (fn []
      [:nav {:class "container px-6 py-4 mx-auto md:flex md:justify-between md:items-center"}
       [:div {:class "flex items-center justify-between"}
        [:a {:class "text-xl font-bold text-white transition-colors duration-300 transform md:text-2xl hover:text-indigo-400"
             :href brand-link} brand]
     ; Mobile menu button
        [:div {:on-click #(swap! open? not)
               :class "flex md:hidden"}
         [:button {:type "button"
                   :class "text-gray-200 hover:text-gray-400 focus:outline-none focus:text-gray-400"
                   :aria-label "toggle menu"}
          [:svg {:view-box "0 0 24 24"
                 :class "w-6 h-6 fill-current"}
           [:path {:fill-rule "evenodd"
                   :d "M4 5h16a1 1 0 0 1 0 2H4a1 1 0 1 1 0-2zm0 6h16a1 1 0 0 1 0 2H4a1 1 0 0 1 0-2zm0 6h16a1 1 0 0 1 0 2H4a1 1 0 0 1 0-2z"}]]]]]
    ; Mobile Menu open: \"block\", Menu closed: \"hidden\"

       (into [:div {:class (str (if @open? "flex" "hidden")
                                " flex-col mt-2 space-y-4 md:flex md:space-y-0 md:flex-row md:items-center md:space-x-10 md:mt-0")}]
             (map menu-item items))])))

(defn splash-message [{:keys [title title-small link-text link-url]}]
  [:section {:class "flex items-center justify-center"
             :style {:height "500px"}}
   [:div {:class "text-center"}
    [:p {:class "text-xl font-medium tracking-wider text-gray-300"}
     title-small]
    (into-text [:h2 {:class "mt-6 text-3xl font-bold text-white md:text-5xl"}]
               title)
    [:div {:class "flex justify-center mt-8"}
     [:a {:class "px-8 py-2 text-lg font-medium text-white transition-colors duration-300 transform bg-indigo-600 rounded hover:bg-indigo-500"
          :href link-url} link-text]]]])

(defn header [{:keys [nav splash]}]
  [:header {:class "bg-gray-800"}
   [comp-nav nav]
   (when splash
     [splash-message splash])])

;; footer

(defn footer [{:keys [copyright right]}]
  [:footer {:class "border-t"}
   [:div {:class "container flex items-center justify-between px-6 py-8 mx-auto"}
    [:p {:class "text-gray-500"} copyright]
    [:p {:class "font-medium text-gray-700"} right]]])

;; components

(defn- col [{:keys [title text]}]
  [:div {:class "px-6 py-8 overflow-hidden bg-white rounded-md shadow-md"}
   [:h2 {:class "text-xl font-medium text-gray-800"}
    title]
   [:p {:class "max-w-md mt-4 text-gray-400"}
    text]])

(defn cols-three [{:keys [title link-href link-text cols]}]
  (let [[a b c] cols]
    [:section {:class "bg-white"}
     [:div {:class "max-w-5xl px-6 py-16 mx-auto"}
      [:div {:class "md:flex md:justify-between"}
       (into-text [:h2 {:class "text-3xl font-semibold text-gray-800"}] title)
       [:a {:href link-href
            :class "block mt-6 text-indigo-700 underline md:mt-0"}
        link-text]]
      [:div {:class "grid gap-8 mt-10 md:grid-cols-2 lg:grid-cols-3"}
       [col a]
       [col b]
       [col c]]]]))

(defn message-button [{:keys [title text link-text link-href]}]
  [:section {:class "bg-white"}
   [:div {:class "max-w-5xl px-6 py-16 mx-auto"}
    [:div {:class "px-8 py-12 bg-gray-800 rounded-md md:px-20 md:flex md:items-center md:justify-between"}
     [:div
      (into-text
       [:h3 {:class "text-2xl font-semibold text-white"}]
       title)
      [:p {:class "max-w-md mt-4 text-gray-400"} text]]
     [:a {:class "block px-8 py-2 mt-6 text-lg font-medium text-center text-white transition-colors duration-300 transform bg-indigo-600 rounded md:mt-0 hover:bg-indigo-500"
          :href link-href} link-text]]]])

(defn foto-bottom [{:keys [title text img-url]}]
  [:section {:class "bg-white"}
   [:div {:class "max-w-5xl px-6 py-16 mx-auto text-center"}
    (into
     [:h2 {:class "text-3xl font-semibold text-gray-800"}]
     title)
    [:p {:class "max-w-lg mx-auto mt-4 text-gray-600"}
     text]
    [:img {:class "object-cover object-center w-full mt-16 rounded-md shadow h-80"
           :src img-url}]]])

;; components that need refactoring

(defn foto-right []
  [:section {:class "bg-white"}
   [:div {:class "max-w-5xl px-6 py-16 mx-auto"}
    [:div {:class "items-center md:flex md:space-x-6"}
     [:div {:class "md:w-1/2"}
      [:h3 {:class "text-2xl font-semibold text-gray-800"}
       "Lorem ipsum dolor sit "
       [:br]
       " amet, consectetur"]
      [:p {:class "max-w-md mt-4 text-gray-600"}
       "Duis aute irure dolor in reprehenderit in voluptate velit esse\n                        cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in\n                        culpa qui officia deserunt mollit anim id est laborum."]
      [:a {:href "#"
           :class "block mt-8 text-indigo-700 underline"}
       "Experienced team"]]
     [:div {:class "mt-8 md:mt-0 md:w-1/2"}
      [:div {:class "flex items-center justify-center"}
       [:div {:class "max-w-md"}
        [:img {:class "object-cover object-center w-full rounded-md shadow"
               :style {:height "500px"}
               :src "https://images.unsplash.com/photo-1618346136472-090de27fe8b4?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=673&q=80"}]]]]]]])

(defn foto-left []
  [:section {:class "bg-white"}
   [:div {:class "max-w-5xl px-6 py-16 mx-auto"}
    [:div {:class "items-center md:flex md:space-x-6"}
     [:div {:class "md:w-1/2"}
      [:div {:class "flex items-center justify-center"}
       [:div {:class "max-w-md"}
        [:img {:class "object-cover object-center w-full rounded-md shadow"
               :style {:height "500px"}
               :src "https://images.unsplash.com/photo-1616874535244-73aea5daadb9?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"}]]]]
     [:div {:class "mt-8 md:mt-0 md:w-1/2"}
      [:h3 {:class "text-2xl font-semibold text-gray-800"}
       "Lorem ipsum dolor sit "
        ;[:br] 
        ;" amet, consectetur"
       ]
      [:p {:class "max-w-md mt-4 text-gray-600"}
       "Duis aute irure dolor in reprehenderit in voluptate velit essecillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt inculpa qui officia deserunt mollit anim id est laborum."]
      [:a {:href "#"
           :class "block mt-8 text-indigo-700 underline"}
       "Experienced team"]]]]])

(defn people []
  [:section {:class "bg-white"}
   [:div {:class "max-w-5xl px-6 py-16 mx-auto text-center"}
    [:h2 {:class "text-3xl font-semibold text-gray-800"} "Our Leadership"]
    [:p {:class "max-w-lg mx-auto mt-4 text-gray-600"} "Duis aute irure dolor in reprehenderit in voluptate velit esse\n                cillum\n                dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia\n                deserunt mollit anim id est laborum."]
    [:div {:class "grid gap-8 mt-6 md:grid-cols-2 lg:grid-cols-4"}
     [:div
      [:img {:class "object-cover object-center w-full h-64 rounded-md shadow", :src "https://images.unsplash.com/photo-1614030126544-b79b92e29e98?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"}]
      [:h3 {:class "mt-2 font-medium text-gray-700"} "John Doe"]
      [:p {:class "text-sm text-gray-600"} "CEO"]]
     [:div
      [:img {:class "object-cover object-center w-full h-64 rounded-md shadow", :src "https://images.unsplash.com/photo-1614030126544-b79b92e29e98?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"}]
      [:h3 {:class "mt-2 font-medium text-gray-700"} "John Doe"]
      [:p {:class "text-sm text-gray-600"} "CEO"]]
     [:div
      [:img {:class "object-cover object-center w-full h-64 rounded-md shadow", :src "https://images.unsplash.com/photo-1614030126544-b79b92e29e98?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"}]
      [:h3 {:class "mt-2 font-medium text-gray-700"} "John Doe"]
      [:p {:class "text-sm text-gray-600"} "CEO"]]
     [:div
      [:img {:class "object-cover object-center w-full h-64 rounded-md shadow", :src "https://images.unsplash.com/photo-1614030126544-b79b92e29e98?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"}]
      [:h3 {:class "mt-2 font-medium text-gray-700"} "John Doe"]
      [:p {:class "text-sm text-gray-600"} "CEO"]]]]])

(defn fotos-with-text []
  [:section {:class "bg-white"}
   [:div {:class "max-w-5xl px-6 py-16 mx-auto space-y-8 md:flex md:items-center md:space-y-0"}
    [:div {:class "md:w-2/3"}
     [:div {:class "hidden md:flex md:items-center md:space-x-10"}
      [:img {:class "object-cover object-center rounded-md shadow w-72 h-72"
             :src "https://images.unsplash.com/photo-1614030126544-b79b92e29e98?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"}]
      [:img {:class "object-cover object-center w-64 rounded-md shadow h-96"
             :src "https://images.unsplash.com/photo-1618506469810-282bef2b30b3?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80"}]]
     [:h2 {:class "text-3xl font-semibold text-gray-800 md:mt-6"}
      "Lorem ipsum dolor "]
     [:p {:class "max-w-lg mt-4 text-gray-600"}
      "Duis aute irure dolor in reprehenderit in voluptate velit esse illum\n                    dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui\n                    officia\n                    deserunt mollit anim id est laborum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat\n                    non proident, sunt in culpa qui officia\n                    deserunt mollit anim id est laborum."]]
    [:div {:class "md:w-1/3"}
     [:img {:class "object-cover object-center w-full rounded-md shadow"
            :style {:height "700px"}
            :src "https://images.unsplash.com/photo-1593352216840-1aee13f45818?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"}]]]])

(defn bullet-points []
  [:section {:class "bg-white"}
   [:div {:class "max-w-5xl px-6 py-16 mx-auto"}
    [:h2 {:class "text-3xl font-semibold text-gray-800"} "Lorem ipsum dolor sit amet, "
     [:br] " consectetur adipiscing"]
    [:p {:class "max-w-lg mt-4 text-gray-600"} "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum\n                dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia\n                deserunt mollit anim id est laborum."]
    [:div {:class "grid gap-8 mt-10 md:mt-20 md:grid-cols-2"}
     [:div {:class "flex space-x-4"}
      [:svg {:class "w-6 h-6 text-gray-500"
             :view-box "0 0 50 50"
             :fill "none"
             :xmlns "http://www.w3.org/2000/svg"}
       [:path {:d "M47.6268 23.7062C48.2466 24.4484 48.2466 25.5277 47.6268 26.2699L44.4812 30.0372C43.803 30.8493 43.4742 31.8971 43.5669 32.9512L44.0044 37.9287C44.0912 38.9165 43.4411 39.8188 42.4765 40.0491L38.0619 41.1031C36.9808 41.3612 36.0559 42.0575 35.5089 43.025L33.2053 47.099C32.6961 47.9995 31.5844 48.3631 30.6415 47.9375L26.6498 46.1358C25.6003 45.6621 24.3976 45.6636 23.3493 46.14L19.3597 47.9531C18.4161 48.3819 17.3014 48.0189 16.7912 47.1168L14.4911 43.0489C13.9441 42.0814 13.0192 41.3851 11.9381 41.127L7.52286 40.0728C6.55849 39.8426 5.90838 38.9406 5.99496 37.9529L6.43346 32.9505C6.52583 31.8968 6.19706 30.8494 5.5191 30.0375L2.37029 26.2665C1.75138 25.5253 1.75043 24.4477 2.36803 23.7054L5.52362 19.9127C6.1988 19.1012 6.52582 18.0557 6.43339 17.0041L5.99624 12.0308C5.90922 11.0408 6.56225 10.1372 7.52946 9.90904L11.9298 8.87123C13.0153 8.61522 13.9446 7.91765 14.4935 6.94684L16.7947 2.87709C17.3039 1.97664 18.4156 1.61302 19.3585 2.03858L23.3544 3.8422C24.4007 4.31444 25.5993 4.31444 26.6456 3.8422L30.6415 2.03858C31.5844 1.61301 32.6961 1.97663 33.2053 2.87709L35.5089 6.95112C36.0559 7.9186 36.9808 8.61486 38.0619 8.87297L42.4765 9.92701C43.4411 10.1573 44.0912 11.0596 44.0044 12.0474L43.5669 17.0249C43.4742 18.079 43.803 19.1268 44.4812 19.939L47.6268 23.7062ZM25 37.9326C26.8075 37.9326 28.2727 36.4674 28.2727 34.6599V34.4275C28.2727 32.6201 26.8075 31.1548 25 31.1548C23.1925 31.1548 21.7273 32.6201 21.7273 34.4275V34.6599C21.7273 36.4674 23.1925 37.9326 25 37.9326ZM25 28.377C26.8075 28.377 28.2727 26.9117 28.2727 25.1042V15.3162C28.2727 13.5087 26.8075 12.0435 25 12.0435C23.1925 12.0435 21.7273 13.5087 21.7273 15.3162V25.1042C21.7273 26.9117 23.1925 28.377 25 28.377Z", :stroke "currentColor", :stroke-width "2"}]]
      [:div
       [:h4 {:class "text-xl font-medium text-gray-800"} "Design concept"]
       [:p {:class "max-w-lg mt-4 text-gray-600"} "Vitae nulla nunc euismod vel nunc euismod velpretium tellus\n                            accumsan nulla nunc euismod ve semper."]]]
     [:div {:class "flex space-x-4"}
      [:svg {:class "w-6 h-6 text-gray-500"
             :view-box "0 0 50 50"
             :fill "none"
             :xmlns "http://www.w3.org/2000/svg"}
       [:path {:d "M1 25C1 11.8023 11.8023 1 25 1C38.1977 1 49 11.8023 49 25C49 38.1977 38.1977 49 25 49C11.8023 49 1 38.1977 1 25ZM33.36 35.3573C34.7228 36.1959 36.5074 35.771 37.346 34.4082C38.1913 33.0346 37.7522 31.2351 36.3692 30.4053L28.221 25.5164C27.6186 25.155 27.25 24.504 27.25 23.8014V14.375C27.25 12.7872 25.9628 11.5 24.375 11.5C22.7872 11.5 21.5 12.7872 21.5 14.375V25.8236C21.5 27.2127 22.2206 28.5023 23.4036 29.2302L33.36 35.3573Z", :stroke "currentColor", :stroke-width "2"}]]
      [:div
       [:h4 {:class "text-xl font-medium text-gray-800"} "Developing websites"]
       [:p {:class "max-w-lg mt-4 text-gray-600"} "Vitae nulla euismod velpretium tellus accumsan nulla nunc\n                            euismod ve semper. Vitae nulla euismod velpretium tellus\n                            accumsan nulla nunc euismod ve semper. Vitae nulla euismod velpretium tellus accumsan nulla nunc\n                            euismod ve semper."]]]]]])





