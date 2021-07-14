(ns goldly-server.helper.site
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
  [template/header-menu {:brand "Your Application"
                         :brand-link "/"
                         :items [{:text "goldly" :link "/goldly/about"}

                                 {:text "status" :link "/goldly/status"}
                                 {:text "theme" :link "/goldly/theme"}

                                 {:text "repl" :link "/repl"}
                                 {:text "notebooks" :link "/goldly/notebooks"}
                                 {:text "nrepl" :link "/goldly/nrepl"}

                                 {:text "snippets" :link "/system/snippet-registry"}
                                 {:text "running systems" :link "/goldly/systems"}

                 ;{:text "notebook" :link "/notebook-test"}
                                 {:text "feedback" :link "https://github.com/pink-gorilla/goldly/issues" :special? true}]}])


