(ns goldly.web.routes
  (:require
   [bidi.bidi :as bidi]
   [bidi.ring :refer [resources]]))

(def app-routes
  ["/" {"" :index
        "section-a" {"" :section-a
                     ["/item-" :item-id] :a-item}
        "section-b" :section-b
        "missing-route" :missing-route
        true :four-o-four}])

(def routes
  ["" {"a-items" {"" :a-items
                  ["/" :item-id] :a-item}

       "/this-route" {""                {:get :handler-1}
                      "/something"      {:get :handler-2}
                      "/something-else" {:get :handler-3}}}])

(comment
  (bidi/match-route routes "/this-route" :request-method :get)
  (bidi/match-route routes "/this-route/something" :request-method :get)
  (bidi/match-route routes "/this-route/something-else" :request-method :get)
  (bidi/match-route routes "a-items" :request-method :get)
  (bidi/match-route routes "a-items/kjhkjk" :request-method :get))

(def serve-index-page
  (bidi/handler (java.io.File. "resources/public/index.html")))

;; the relevant routing part
(def routes2
  ["/"
    [#"^$" serve-index-page]                          ;; redirect / to index.html
    ["" (bidi.ring/resources {:prefix "public/"})]]) ;; else serve file from resources dir


(bidi/match-route routes2 "/" :request-method :get)




