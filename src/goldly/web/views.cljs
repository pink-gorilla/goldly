(ns goldly.web.views
  (:require
   [taoensso.timbre :as timbre :refer-macros (tracef debugf infof warnf errorf info)]
   [re-frame.core :refer [clear-subscription-cache! subscribe]]
   [bidi.bidi :as bidi]
   [goldly.web.routes :refer [app-routes]]
   [goldly.puppet.loader :refer [system]]))

(defn main-page []
  (let [systems (subscribe [:systems])
        _ (info "main-page showing: systems: " @systems)]
    [:<>
     [:h1 "running systems: " (count @systems)]
     [:ul
      (for [{:keys [id name]} @systems]
        ^{:key id}
        [:li.m-3
         [:a {:class "m-3 bg-yellow-300"
              :href (bidi/path-for app-routes :system :system-id id)} name]])]])) ; (str "/system/" id) "#/system/"

(defn goldly-app-page []
  (let [route (subscribe [:route])
        {:keys [handler route-params]} @route]
    [:div ;.w.container
     (case handler
       :main [main-page]
       :system [system (:system-id route-params)]
       [main-page])]))

;{:route-params {:item-id "1"}, :handler :a-item}


#_(defmulti page-contents identity)

#_(defmethod page-contents :index []
    [:span
     [:h1 "Routing example: Index"]
     [:ul
      [:li [:a {:href (bidi/path-for app-routes :section-a)} "Section A"]]
      [:li [:a {:href (bidi/path-for app-routes :section-b)} "Section B"]]
      [:li [:a {:href (bidi/path-for app-routes :missing-route)} "Missing-route"]]
      [:li [:a {:href "/borken/link"} "Borken link"]]]])

#_(defn page []
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

