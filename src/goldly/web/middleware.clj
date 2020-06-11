(ns goldly.web.middleware
  "a middleware takes a handler, and wraps a middleware around it.
   It is handler transformation, not routing related."
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
   [ring.middleware.json :refer [wrap-json-response]]))

(defn wrap-api-handler
  "a wrapper for JSON API calls
   from pinkgorilla notebook
   "
  [handler]
  (-> handler
      (wrap-defaults api-defaults)
      (wrap-restful-format :formats [:json :transit-json :edn])))

(defn wrap-app [handler]
  (-> handler
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
      (wrap-gzip))) ;oz 



