(ns goldly.web.handler
  (:require
   [clojure.string]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.cljsjs :refer [wrap-cljsjs]]
   [ring.middleware.gzip :refer [wrap-gzip]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.format :refer [wrap-restful-format]]
   [ring.middleware.json :refer [wrap-json-response]]
   [compojure.core :as compojure :refer [defroutes routes context GET POST]]
   [compojure.route :refer [files resources not-found] :as compojure-route]
   [compojure.handler :as handler]
   [goldly.web.routes :refer [app-handler resource-handler]]
   ))

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

