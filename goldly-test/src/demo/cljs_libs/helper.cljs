
;; clj-libs is here, because 
;; it shows that we can add custom libraries inside sci source code files.

(defn add-3 [a]
  (+ a 3))


(def header
  [header-menu 
   {:brand "Your Application"
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



