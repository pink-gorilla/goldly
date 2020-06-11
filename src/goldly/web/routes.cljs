(ns goldly.web.routes
  (:require
   [bidi.bidi :as bidi]
   [pushy.core :as pushy]))

; see: 
; https://github.com/clj-commons/pushy

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

(def state (atom {}))

(def app-routes
  ["/" {"" :index
        "section-a" {"" :section-a
                     ["/item-" :item-id] :a-item}
        "section-b" :section-b
        "missing-route" :missing-route
        true :four-o-four}])


(defn set-page! [match]
  (swap! state assoc :page match))

(def history
  (pushy/pushy set-page! (partial bidi/match-route app-routes)))

(pushy/start! history)