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
            "ui-input" "ui-repl" "ui-vega" "ui-highcharts" 
            "ui-code" "ui-markdown" "ui-math" "ui-site"
            "ui-quil" "ui-binaryclock" "ui-leaflet"
            "kernel-cljs-shadow"])

^:R 
(defn artefacts []  
  ^:R
  [:div
   [:h1.text-3xl.text-blue-900 "PinkGorilla Artefacts (clojars)"]
   (into [:div]
         (map project names))])

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defmethod reagent-page :user/artefacts [{:keys [route-params query-params handler] :as route}]
  [:div
   [link-href "/" "main"]
   [artefacts]])