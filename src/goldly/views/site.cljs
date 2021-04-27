(ns goldly.views.site
  (:require
   [goldly.template :as template]))

(def h-splash
  {:nav {:brand "Goldly"
         :brand-link "https://github.com/pink-gorilla/goldly"
         :items [{:text "running systems" :link "/goldly"}
                 {:text "feedback" :link "https://github.com/pink-gorilla/gorilla-notebook/issues" :special true}]}
   :splash {:link-text "On Github"
            :link-url "https://github.com/pink-gorilla/goldly"
            :title ["Goldly lets you create "
                    [:br]
                    "realtime dashboards powered by clojure"]
            :title-small "open source"}})

(def h (dissoc h-splash :splash))

(defn header-splash []
  [template/header h-splash])

(defn header []
  [template/header h])

