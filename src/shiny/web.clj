(ns shiny.web
  (:require
   [clojure.string]
   [clojure.tools.logging :as log]
   [ring.util.response :as response]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.gzip :refer [wrap-gzip]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.format :refer [wrap-restful-format]]
   [ring.middleware.anti-forgery :refer (*anti-forgery-token*)]
   [ring.middleware.json :refer [wrap-json-response]]
   [compojure.core :as compojure :refer [defroutes routes context GET POST]]
   [compojure.route :refer [files resources not-found] :as compojure-route]
   [compojure.handler :as handler]
   [org.httpkit.server :as httpkit]
   [hiccup.page :as page]
   [shiny.core]
   [shiny.ws :refer [start-router! pinkie-routes]]))

(defn unique-id
  "Get a unique id."
  []
  (str (java.util.UUID/randomUUID)))

(defn session-uid
  "Get session uuid from a request."
  [req]
  (get-in req [:session :uid]))


(defn app [req]
  (let [csrf-token (force *anti-forgery-token*) ;(:anti-forgery-token ring-req)] ; Also an option
        session (if (session-uid req)
                  (:session req)
                  (assoc (:session req) :uid (unique-id)))]

    (response/content-type
     {:status 200
      :session session
      :body
      (page/html5
       [:head
        [:title "Shiny"]
        #_[:link {:type "text/css"
                  :href "https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.7/css/bootstrap.min.css"
                  :rel "stylesheet"}]]
       [:body
        [:div#sente-csrf-token {:data-csrf-token csrf-token}]
        [:div#app]
        [:script {:src "main.js" :type "text/javascript"}]])} 
     "text/html")))


(defroutes resource-handlers
  (resources "/")  ;; Needed during development
  (resources "/" {:root "gorilla-repl-client"})
  (files "/" {:root "./target"})
  (GET "/" req (app req))
  (not-found "Bummer, not found"))


;; DEFAULT HANDLER

(defn wrap-api-handler
  "a wrapper for JSON API calls"
  [handler]
  (-> handler
      (wrap-defaults api-defaults)
      (wrap-restful-format :formats [:json :transit-json :edn])))


(defroutes default-routes
  #_(-> app-routes
        (wrap-api-handler)
        (wrap-cors-handler))
  (-> pinkie-routes
      (wrap-defaults
       (-> site-defaults
           (assoc-in [:security :anti-forgery] true)))
      (wrap-keyword-params)
      (wrap-params)
      ;(wrap-api-handler)
      ;(wrap-cljsjs)
      (wrap-session)
      (wrap-json-response)
      (wrap-gzip))
  resource-handlers)

(def default-handler
  (wrap-session default-routes))

;; webserver

(def server (atom nil))

(defn server-start!
  "starts webserver with websockets"
  [options]
  (println "starting web at " (:port options))
  (httpkit/run-server default-handler {:port (:port options)}) ; (handler/site app-routes)  
  (start-router!)
  (reset! server nil))