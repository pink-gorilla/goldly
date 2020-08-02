(ns goldly.web.routes
  (:require
   [webly.web.resources :refer [resource-handler]]))

(def goldly-routes-app
  {"" :ui/system-list
   ["system/" :system-id] :ui/system})

(def goldly-routes-api
  {"token"  :ws/token
   "chsk"  {:get  :ws/chsk-get
            :post :ws/chsk-post}})

(def goldly-routes-frontend
  ["/" goldly-routes-app])

(def goldly-routes-backend
  ["/" {"" goldly-routes-app
        "api/" goldly-routes-api
        "r" resource-handler}])
