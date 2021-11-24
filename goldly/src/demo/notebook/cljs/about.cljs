


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
                                 ;{:text "notebooks" :link "/goldly/notebooks"}
                                 {:text "nrepl" :link "/goldly/nrepl"}

                                 {:text "snippets" :link "/system/snippet-registry"}
                                 {:text "running systems" :link "/goldly/systems"}

                 ;{:text "notebook" :link "/notebook-test"}
                                 {:text "feedback" :link "https://github.com/pink-gorilla/goldly/issues" :special? true}]}])

(defn about []
  (let [routes (subscribe [:webly/routes])]
    (fn []
      (let [href-systems (bidi/path-for (:client routes) :goldly/system-list)]

        [:div
         [header]
         [splash]
         [t/cols-three {:title ["Use-cases"
                                [:br]
                                "just by using clojure"]
                        :link-text "Experienced team"
                        :link-href "#"
                        :cols [{:title "Dashboard" :text "Dashboards can load data from the server. Allow your users to change what they want to see"}
                               {:title "Notebook" :text "In a notebook you can use it for data exploration."}
                               {:title "Embedded" :text "Embed it to another website"}]}]

         [t/footer {:copyright "© 2019-2021 Pink Gorilla"
                    :right "Served by Goldly"}]]))))

