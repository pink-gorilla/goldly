(ns goldly.web.views
  (:require
   [bidi.bidi :as bidi]
   [goldly.web.routes :refer [app-routes]]))

(defmulti page-contents identity)

(defmethod page-contents :index []
  [:span
   [:h1 "Routing example: Index"]
   [:ul
    [:li [:a {:href (bidi/path-for app-routes :section-a)} "Section A"]]
    [:li [:a {:href (bidi/path-for app-routes :section-b)} "Section B"]]
    [:li [:a {:href (bidi/path-for app-routes :missing-route)} "Missing-route"]]
    [:li [:a {:href "/borken/link"} "Borken link"]]]])

(defn page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [:p [:a {:href (bidi/path-for app-routes :index)} "Go home"]]
       [:hr]
       (page-contents page) ;;
       [:hr]
       [:p "(Using "
        [:a {:href "https://reagent-project.github.io/"} "Reagent"] ", "
        [:a {:href "https://github.com/juxt/bidi"} "Bidi"] " & "
        [:a {:href "https://github.com/venantius/accountant"} "Accountant"]
        ")"]])))

