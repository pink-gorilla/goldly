
(defn block [{:keys [name class]}  & children]
  (into [:div.bg-blue-400.m-5.inline-block
         {:class class}
         [:h1.text-2xl name]]
        children))

(defn svg [url-link url-img p]
  [:a {:href (format url-link p)}
   [:img.inline-block
    {:src
     (format url-img p)}]])

(defn project [p]
  [:p ; p
   [svg "https://github.com/pink-gorilla/%s/actions?workflow=CI"
    "https://github.com/pink-gorilla/%s/workflows/CI/badge.svg" p]

   [svg "https://codecov.io/gh/pink-gorilla/%s"
    "https://codecov.io/gh/pink-gorilla/%s/branch/master/graph/badge.svg" p]

   [svg "https://clojars.org/org.pinkgorilla/%s"
    "https://img.shields.io/clojars/v/org.pinkgorilla/%s.svg" p]])


(def names ["webly" "goldly" "notebook" "pinkie"
            "nrepl-middleware" "notebook-encoding" "gorilla-explore"
            "picasso" "gorilla-ui"
            "kernel-cljs-shadow"])

(defn artefacts []
  [:div
   [:h1.text.xl.text-blue-900 "PinkGorilla Artefacts Cookies (Snippet)"]
   (into [:div]
         (map project names))])


(println "artefacts: " (artefacts))

