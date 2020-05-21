(ns shiny.web
  (:require
   [clojure.string]
   [compojure.core :as compojure :refer [defroutes routes context GET POST]]
   [compojure.route :refer [files resources not-found] :as compojure-route]
   [compojure.handler :as handler]

   [ring.adapter.jetty :as ring]
   [clojure.tools.logging :as log]
   [hiccup.page :as page]

   [shiny.core]))

(def app
  (page/html5
   [:head
    [:title "Shiny"]
    [:link {:type "text/css"
            :href "https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.7/css/bootstrap.min.css"
            :rel "stylesheet"}]
    [:link {:type "text/css"
            :href "https://getbootstrap.com/docs/3.3/examples/starter-template/starter-template.css"
            :rel "stylesheet"}]]
   [:body
    [:div.container
     [:nav.navbar.navbar-inverse.navbar-fixed-top
      [:div.container
       [:div.navbar-header
        [:a.navbar-brand "Shiny clj"]]
       [:div#navbar.collapse.navbar-collapse
        [:ul.nav.navbar-nav
         [:li
          [:a {:href "#"} "Home"]]]]]]
     [:div#app]
     [:script {:src "main.js" :type "text/javascript"}]]]))


(defroutes app-routes
  (resources "/")  ;; Needed during development
  (resources "/" {:root "gorilla-repl-client"})
  (files "/" {:root "./target"})
  (GET "/" [] app)
  (not-found "Bummer, not found"))

;; webserver

(def server (atom nil))

(defn server-start!
  "starts webserver with websockets"
  [options]
  (println "starting web at " (:port options))
  (ring/run-jetty (handler/site app-routes) {:port (:port options)})
  (reset! server nil))