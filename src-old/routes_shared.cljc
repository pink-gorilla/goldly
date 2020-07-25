(ns goldly.web.routes-shared
  (:require
   [taoensso.timbre :as timbre :refer-macros (tracef debugf infof warnf errorf info)]
   [bidi.bidi :as bidi]
   ;[pushy.core :as pushy]
   ))

(def app-routes
  ["" {""               :main
       ["/system/" :system-id] :system
       "section-a"      {"" :section-a
                         ["/item-" :item-id] :a-item
                         ["/i-" :item-id "-" :u] :a-item}
       "section-b"     :section-b
        ;true           :four-o-four
       ["" :id]        :bongo}])

(bidi/path-for app-routes :main)
(bidi/path-for app-routes :section-a)
(bidi/path-for app-routes :system :system-id 88)
(bidi/path-for app-routes :section-a :a-item 8)

(defn GET [url]
  (bidi/match-route app-routes url))

(GET "section-a")

(GET "section-a/item-1")
;; => {:route-params {:item-id "1"}, :handler :a-item}

(GET "section-a/i-1-7hy")

