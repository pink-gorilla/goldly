(ns goldly.web.middleware
  "a middleware takes a handler, and wraps a middleware around it.
   It is handler transformation, not routing related."
  (:require
   [clojure.string]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.cljsjs :refer [wrap-cljsjs]]
   [ring.middleware.gzip :refer [wrap-gzip]]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.format :refer [wrap-restful-format]]
   [ring.middleware.json :refer [wrap-json-response]]))


;from clojurewb - good example for middleware for websocket requests
#_(defn wrap-formats2 [handler]
  (let [wrapped (-> handler wrap-params (wrap-format formats/instance))]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn wrap-api-handler
  "a wrapper for JSON API calls
   from pinkgorilla notebook
   "
  [handler]
  (-> handler
      (wrap-defaults api-defaults)
      (wrap-restful-format :formats [:json :transit-json :edn])))

(defn wrap-stateful-api-handler
  "a wrapper for JSON API calls
   from pinkgorilla notebook
   "
  [handler]
  (-> handler ; middlewares execute from bottom -> up
      (wrap-anti-forgery)

      (wrap-defaults api-defaults)
      (wrap-keyword-params)
      (wrap-params)
      ;(wrap-cljsjs)
      (wrap-restful-format :formats [:json :transit-json :edn])
      (wrap-gzip)))

#_(defn wrap-ws [handler]
    (-> handler
        #_(wrap-defaults
           (-> site-defaults
               (assoc-in [:security :anti-forgery] true)))

        (wrap-defaults api-defaults)
      ;(wrap-api-handler)
      ;(wrap-cors-handler)
        (wrap-cljsjs) ; oz
      ;(wrap-session)
      ;(wrap-json-response)
        (wrap-gzip))) ;oz 

(defn wrap-goldly [handler]
  (-> handler
      (wrap-defaults site-defaults)
      (wrap-session)
      wrap-keyword-params
      wrap-params
      (wrap-cljsjs)
      (wrap-gzip)))

;; Add necessary Ring middleware:
;      ring.middleware.keyword-params/wrap-keyword-params
;ring.middleware.params/wrap-params
