(ns goldly-server.site
  (:require
   [ui.site.template :as template]))

(defn splash []
  [template/splash-message {:link-text "On Github"
                            :link-url "https://github.com/pink-gorilla/goldly"
                            :title ["Goldly lets you create "
                                    [:br]
                                    "realtime dashboards powered by clojure"]
                            :title-small "open source"}])

(defn header []
  [template/header-menu {:brand "Goldly"
                         :brand-link "/"
                         :items [{:text "running systems" :link "/goldly"}
                 ;{:text "notebook" :link "/notebook-test"}
                                 {:text "feedback" :link "https://github.com/pink-gorilla/goldly/issues" :special? true}]}])


