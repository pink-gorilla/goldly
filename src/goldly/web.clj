(ns goldly.web
  (:require
   [clojure.string]
   [clojure.tools.logging :as log]
   [ring.util.response :as response]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.cljsjs :refer [wrap-cljsjs]]
   [ring.middleware.gzip :refer [wrap-gzip]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.format :refer [wrap-restful-format]]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [ring.middleware.json :refer [wrap-json-response]]
   [compojure.core :as compojure :refer [defroutes routes context GET POST]]
   [compojure.route :refer [files resources not-found] :as compojure-route]
   [compojure.handler :as handler]
   [org.httpkit.server :as httpkit]
   [hiccup.page :as page]
   [goldly.core]
   [goldly.ws :refer [start-router! ws-handler]]))

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
        [:title "goldly"]
        [:link {:rel "stylesheet" :href "tailwindcss/dist/tailwind.css" :type "text/css"}]]
       [:body
        [:div#sente-csrf-token {:data-csrf-token csrf-token}]
        [:div#app]
        [:script {:src "main.js" :type "text/javascript"}]])}
     "text/html")))

(defroutes resource-handler
  (resources "/")  ;; Needed during development
  (resources "/" {:root "gorilla-repl-client"})
  (files "/" {:root "./target"})
  (files "/" {:root "./node_modules"}) ; access css and bundles in npm dependencies  
  (not-found "Bummer, not found"))

(defroutes app-handler
  (GET "/" req (app req))
  ws-handler)

;; DEFAULT HANDLER

(defn wrap-api-handler
  "a wrapper for JSON API calls
   from pinkgorilla notebook
   "
  [handler]
  (-> handler
      (wrap-defaults api-defaults)
      (wrap-restful-format :formats [:json :transit-json :edn])))

(defroutes default-handler
  (-> app-handler
      (wrap-defaults site-defaults)
      #_(wrap-defaults
         (-> site-defaults
             (assoc-in [:security :anti-forgery] true)))
      ;(wrap-keyword-params)
      ;(wrap-params)
      ;(wrap-api-handler)
      ;(wrap-cors-handler)
      (wrap-cljsjs) ; oz
      ;(wrap-session)
      ;(wrap-json-response)
      (wrap-gzip)) ;oz
  resource-handler)


;; webserver


(def server (atom nil))

(defn server-start!
  "starts webserver with websockets"
  [options]
  (println "starting web at " (:port options))
  (httpkit/run-server default-handler {:port (:port options)}) ; (handler/site app-routes)  
  (start-router!)
  (reset! server nil))