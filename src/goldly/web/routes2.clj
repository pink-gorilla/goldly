(ns goldly.web.routes2
  (:require
   [clojure.string]
   [compojure.core :as compojure :refer [defroutes routes context GET POST]]
   [compojure.route :refer [files resources not-found] :as compojure-route]
   [goldly.web.views :refer [app]]
   [goldly.web.ws :refer [ws-handler]]
   ))


(defroutes resource-handler
  (resources "/"  {:root ""}) ; serve resources from jars (:root defaults to "public")
  ;(files "/goldly/" {:root "./target/goldly"}) ; compiled cljs
  ;(files "/cljs-runtime/" {:root "./target/cljs-runtime"}) ; compiled cljs
  ;(files "/" {:root "./node_modules"}) ; access css and bundles in npm dependencies
  (files "/" {:root "./profiles/demo/src/systems"}) ; resources of systems
  (not-found "Bummer, not found"))

(defroutes app-handler
  (GET "/" req (app req))
  ws-handler)
