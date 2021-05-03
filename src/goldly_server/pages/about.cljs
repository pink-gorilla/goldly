(ns goldly-server.pages.about
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [bidi.bidi :as bidi]
   [webly.web.handler :refer [reagent-page]]
   [webly.user.template :as t]
   [goldly.views.site :refer [header-splash]]))

(defn link-fn [fun text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defn about []
  (let [routes (subscribe [:webly/routes])]
    (fn []
      (let [href-systems (bidi/path-for (:client routes) :goldly/system-list)]

        [:div
         [header-splash]
         [t/cols-three {:title ["Use-cases"
                                [:br]
                                "just by using clojure"]
                        :link-text "Experienced team"
                        :link-href "#"
                        :cols [{:title "Dashboard" :text "Dashboards can load data from the server. Allow your users to change what they want to see"}
                               {:title "Notebook" :text "In a notebook you can use it for data exploration."}
                               {:title "Embedded" :text "Embed it to another website"}]}]

         #_[t/message-button
            {:tile  "Lorem ipsum dolor sit amet"
             :text "Lorem ipsum dolor sit amet, consectetur adipiscing Ac aliquam ac\n                        volutpat, viverra magna risus aliquam massa."
             :link-text "Get In Touch"
             :link-href "#"}]

         #_[t/foto-bottom
            {:title ["Lorem ipsum dolor sit amet, "
                     [:br] " consectetur adipiscing"]
             :text "Duis aute irure dolor in reprehenderit in voluptate velit esse\n                cillum\n                dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia\n                deserunt mollit anim id est laborum."
             :img-url "https://images.unsplash.com/photo-1600069226367-412873fb0637?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80"}]

         ;[t/people]
         ;[t/foto-right]
         ;[t/fotos-with-text]
         ;[t/bullet-points]
         ;[t/foto-left]

         [t/footer {:copyright "© 2019-2021 Pink Gorilla"
                    :right "Served by Goldly"}]

         #_[:div
            [:h1 "goldly demo"]

            [:p [link-dispatch [:bidi/goto :goldly/system-list] "goldly running systems"]]
            [:p [link-dispatch [:bidi/goto :ui/markdown :file "webly.md"] "webly docs"]]
            [:p [link-dispatch [:reframe10x-toggle] "tenx-toggle"]]]]))))

(defmethod reagent-page :goldly/about [{:keys [route-params query-params handler] :as route}]
  [about])