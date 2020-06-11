(ns goldly.web.views
  (:require
   [clojure.string]
   [ring.util.response :as response]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [hiccup.page :as page]))

(defn unique-id
  "Get a unique id."
  []
  (str (java.util.UUID/randomUUID)))

(defn session-uid
  "Get session uuid from a request."
  [req]
  (get-in req [:session :uid]))

(defn app [req]
  (let [csrf-token (force *anti-forgery-token*) ;(:anti-forgery-token ring-req)] ; Also an option
        session (if (session-uid req)
                  (:session req)
                  (assoc (:session req) :uid (unique-id)))]

    (response/content-type
     {:status 200
      :session session
      :body
      (page/html5
       [:head
        [:title "goldly"]
        [:link {:rel "stylesheet" :href "tailwindcss/dist/tailwind.css" :type "text/css"}]]
       [:body
        [:div#sente-csrf-token {:data-csrf-token csrf-token}]
        [:div#app]
        [:script {:src "main.js" :type "text/javascript"}]])}
     "text/html")))