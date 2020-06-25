(ns goldly.user.auth.middleware
  (:require
   [clojurewb.env :refer [defaults]]
   [cheshire.generate :as cheshire]
   [cognitect.transit :as transit]


   [ring-ttl-session.core :refer [ttl-memory-store]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.cookies :refer [wrap-cookies]]
   
   ;[clj-oauth2.client :as oauth2]
   ;[clj-oauth2.ring :as oauth2-ring]
   [ring.middleware.oauth2 :refer [wrap-oauth2]]
   [goldly.user.config :refer [env]]
   [myconfig :refer [myconfig]]))

(println "oauth profile:" (:name myconfig))

(def my-oauth-profiles
  ; https://github.com/weavejester/ring-oauth2 
  {:github
   {:authorize-uri    "https://github.com/login/oauth/authorize"
    :access-token-uri "https://github.com/login/oauth/access_token"
    :client-id        (get-in myconfig [:github :client-id])
    :client-secret    (get-in myconfig [:github :client-secret])
    :scopes           ["user:email" "gist"]
    :launch-uri       "/oauth2/github"
    :redirect-uri     "/oauth2/github/callback"
    :landing-uri      "/my"}})

; wrap-oauth2 needs to be the first position!
; (defn wrap-base [handler]
;  (-> handler
;      (wrap-oauth2 my-oauth-profiles)
;      (wrap-defaults
;         (-> site-defaults
;            (assoc-in [:security :anti-forgery] false)
;            ;(assoc-in [:session :store] (ttl-memory-store (* 60 30)))
;            (assoc-in [:session :cookie-attrs :same-site] :lax))))

; oauth tokens are stored here:
; [gorillauniverse.github.filesystem :refer [workbooks-for-token]]
; (let [github-token] (-> request :oauth2/access-tokens :github) 
; (workbooks-for-token github-token)