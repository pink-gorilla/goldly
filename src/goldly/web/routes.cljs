(ns goldly.web.routes
  (:require
   [taoensso.timbre :as timbre :refer-macros [tracef debugf infof warnf errorf info]]
   [re-frame.core :refer [dispatch]]
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

(def app-routes
  ["/" {"app"                  :main
        ["system/" :system-id] :system
        "section-a"            {"" :section-a
                                ["/item-" :item-id] :a-item}
        "section-b"            :section-b
        true                   :four-o-four
        ["" :id]               :bongo}])

(defn set-page! [match]
  (info "setting page to: " match)
  (dispatch [:goldly/nav match]))

(def history
  (pushy/pushy set-page! (partial bidi/match-route app-routes)))

(defn init-routes []
  (info "starting pushy")
  (pushy/start! history))