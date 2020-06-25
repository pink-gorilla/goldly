(ns goldly.user.auth.middleware
  (:require
   ;[cheshire.generate :as cheshire]
   ;[cognitect.transit :as transit]

   [ring.util.response :as res]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
   [ring-ttl-session.core :refer [ttl-memory-store]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.cookies :refer [wrap-cookies]]
   [ring.middleware.oauth2 :refer [wrap-oauth2]]
   [goldly.user.secrets.core :refer [secrets]]))

(let [s (secrets)
      {:keys [github name]} s
      {:keys [client-id client-secret]} github]
  (println "oauth2 profile:" name)
  (def my-oauth-profiles
  ; https://github.com/weavejester/ring-oauth2 
    {:github
     {:authorize-uri    "https://github.com/login/oauth/authorize"
      :access-token-uri "https://github.com/login/oauth/access_token"
      :client-id        client-id
      :client-secret    client-secret
      :scopes           ["user:email" "gist"]
      :launch-uri       "/oauth2/github"
      :redirect-uri     "/oauth2/github/callback"
      :landing-uri      "/my"}}))


(defn wrap-oauth [handler]
  ;(wrap-oauth2 handler my-oauth-profiles)
  (-> handler
      (wrap-oauth2 my-oauth-profiles)
      (wrap-defaults
       (-> site-defaults
           (assoc-in [:security :anti-forgery] false)
            ;(assoc-in [:session :store] (ttl-memory-store (* 60 30)))
           (assoc-in [:session :cookie-attrs :same-site] :lax)))))

; wrap-oauth2 needs to be the first position!
; (defn wrap-base [handler]
;  

; oauth tokens are stored here:
; [gorillauniverse.github.filesystem :refer [workbooks-for-token]]
; (let [github-token] (-> request :oauth2/access-tokens :github) 
; (workbooks-for-token github-token)

(defn handler-auth [request]
  ; Once the user is authenticated, a new key is added to every request:
  ;   :oauth2/access-tokens
  (println "oauth2 tokens: " (-> request :oauth2/access-tokens :github))
  (let [github-token (:token (-> request :oauth2/access-tokens :github))
        _ (println "github token: " github-token)]
    ;(println (tentacles.gists/user-gists "awb99" {:oauth-token github-token}))
    (res/response {:token github-token})))