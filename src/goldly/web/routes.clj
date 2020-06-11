(ns goldly.web.routes
  (:require
   [clojure.string]
   [bidi.bidi :as bidi]
   [bidi.ring ;:refer [resources]
    ]
   [compojure.core :as compojure :refer [defroutes routes context GET POST]]
   [compojure.route :refer [files resources not-found] :as compojure-route]
   [goldly.web.views :refer [app]]
   [goldly.web.ws :refer [ws-handler]]))

; old  - compojure

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

; new - bidi

(def routes-app
  ["" {"" :index
        "a-items" {"" :a-items
                   ["/" :item-id] :a-item}
        "section-a" {"" :section-a
                     ["/item-" :item-id] :a-item}
        "section-b" :section-b
        "/this-route" {""                {:get :handler-1}
                       "/something"      {:get :handler-2}
                       "/something-else" {:get :handler-3}}
        "missing-route" :missing-route
        true :four-o-four}])

(comment
  (bidi/match-route routes-app "/this-route" :request-method :get)
  (bidi/match-route routes-app "/this-route/something" :request-method :get)
  (bidi/match-route routes-app "/this-route/something-else" :request-method :get)
  (bidi/match-route routes-app "a-items" :request-method :get)
  (bidi/match-route routes-app "a-items/kjhkjk" :request-method :get)
  ;
  )

(def serve-index-page
  (bidi/handler (java.io.File. "resources/public/index.html")))

;; the relevant routing part
(def routes2
  ;["/"
   ;[#"^$" serve-index-page]                          ;; redirect / to index.html
   ["" {"" (bidi.ring/resources {:prefix "public/"})}]
;  ]
) 

(bidi/match-route routes2 "/jjjj" :request-method :get)




