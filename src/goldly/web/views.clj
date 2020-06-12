(ns goldly.web.views
  (:require
   [clojure.string]
   [hiccup.page :as page]))

(defn layout [{:keys [load-bundle]} page]
  (page/html5
   [:head
    [:meta {:http-equiv "Content-Type"
            :content "text/html; charset=utf-8"}]
    [:title "goldly"]
    [:link {:rel "shortcut icon" :href "/r/favicon.ico"}]
    [:link {:rel "stylesheet" :href "/r/tailwindcss/dist/tailwind.css" :type "text/css"}]]
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



