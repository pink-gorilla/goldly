(ns goldly.web.views
  (:require
   [clojure.string]
   [hiccup.page :as page]))

(defn css [link]
  [:link {:rel "stylesheet"
          :type "text/css"
          :href link}])

(defn load-fonts []
  [:div
   (css "http://fonts.googleapis.com/css?family=Arvo:400,700,400italic,700italic|Lora:400,700,400italic,700italic")
   (css "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,400italic")
   (css "https://fonts.googleapis.com/css?family=Roboto+Condensed:400,300")
   (css "https://cdnjs.cloudflare.com/ajax/libs/material-design-iconic-font/2.2.0/css/material-design-iconic-font.min.css")])

(defn layout [{:keys [load-bundle]} page]
  (page/html5
   [:head
    [:meta {:http-equiv "Content-Type"
            :content "text/html; charset=utf-8"}]

    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0"}]

    [:title "goldly"]
    [:link {:rel "shortcut icon" :href "/r/favicon.ico"}]
    (css "/r/tailwindcss/dist/tailwind.css")
    (load-fonts)]
   [:body
    [:div#goldly
     page]
    (if load-bundle
      [:div
       [:script {:src "/r/main.js" :type "text/javascript"}]
       ; todo: start goldly via explicit command
       #_[:script {:type "text/javascript"} "routing_example.core.init_BANG_ (); "]]

      [:span "bundle not loading!"])]))

(defn app-page [csrf-token]
  (layout {:load-bundle true}
          [:div#app
           [:div#sente-csrf-token {:data-csrf-token csrf-token}]
           [:div#app]]))

(defn not-found-page []
  (layout {:load-bundle false}
          [:div#not-found.bg-red-500.m-5
           [:div#noapp]
           [:h1 "Bummer,  not found!"]]))



