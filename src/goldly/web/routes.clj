(ns goldly.web.routes
  (:require
   [clojure.string]
   [clojure.pprint]
   [bidi.bidi :as bidi]
   [bidi.ring]
   [ring.mock.request :refer (request) :rename {request mock-request}]
   [compojure.core :as compojure :refer [defroutes routes context GET POST]]
   [compojure.route :refer [files resources not-found] :as compojure-route]
   [goldly.web.middleware :refer [wrap-app]]
   [goldly.web.views :refer [app-handler]]
   [goldly.web.ws :refer [ws-handler]]))

; old  - compojure

(defroutes resource-handler
  (resources "/"  {:root ""}) ; serve resources from jars (:root defaults to "public")
  ;(files "/goldly/" {:root "./target/goldly"}) ; compiled cljs
  ;(files "/cljs-runtime/" {:root "./target/cljs-runtime"}) ; compiled cljs
  ;(files "/" {:root "./node_modules"}) ; access css and bundles in npm dependencies
  (files "/" {:root "./profiles/demo/src/systems"}) ; resources of systems
  (not-found "Bummer, not found"))

(defroutes app-handler2
  (GET "/" req (app-handler req))
  ws-handler)

(defroutes goldly-handler
  (-> app-handler2
      wrap-app)
  resource-handler)


; new - bidi

(defn test-handler [req]
  (clojure.pprint/pprint req)
  {:status 200 :body "test"})

(def routes-bidi
 [""
  [; api resonses have priority 1
   ; (serving a resource for an api call would be horrible)
   [["/user/" :userid "/article"]
    (fn [req] {:status 201 :body (:route-params req)})]
   
   ; resources
   ["" (bidi.ring/->ResourcesMaybe {:prefix "public/"})]  
   
   ; the app is greedy.
   ["app" (-> #'app-handler)]
   ["app/" (-> #'app-handler)]
   ["/app" (-> #'app-handler)]
   ["/app/" (-> #'app-handler)]
   [#"^.*$" #'app-handler]  ;; redirect / to index.html
   
   ["/test" (-> #'test-handler (bidi.bidi/tag :index))]
   
   ["/blog"
    [["/index.html" (fn [req] {:status 200 :body "Index"})]
     [["/b"] 'blog-article-handler]
     [["/article/" :id ".html"] 'blog-article-handler]
     [["/archive/" :id "/" :page ".html"] 'archive-handler]]]
                       ;["/images/" 'image-handler]
                       ; (fn [req] {:status 200 :body "Not found"})
   ]])

(def handler
  (bidi.ring/make-handler routes-bidi))

(comment

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


  (bidi/match-route routes-app "/this-route" :request-method :get)
  (bidi/match-route routes-app "/this-route/something" :request-method :get)
  (bidi/match-route routes-app "/this-route/something-else" :request-method :get)
  (bidi/match-route routes-app "a-items" :request-method :get)
  (bidi/match-route routes-app "a-items/kjhkjk" :request-method :get)
  

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

(defn blog-article-handler [req]
  {:k 7})

(defn archive-handler [req]
  {:k 7})


(defn image-handler [req]
  {:k 7})

(GET "/" req (app-handler req))



(handler (mock-request :get "/blog/index.html"))
;{:status 200 :body "Index"}))
  (handler (mock-request :get "/test"))

  (handler (mock-request :get "/user/4/article"))
  (handler (-> (mock-request :put "/user/8888/article")
               (assoc :params {"foo" "bar"})))

 (handler (mock-request :get "kjhjhjk"))
 
 (handler (mock-request :get "/blog/b"))
 (handler (mock-request :get "/blog/article/8.html"))

; huge comment
  )


