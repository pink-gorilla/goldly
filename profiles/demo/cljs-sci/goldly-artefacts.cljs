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

(def main ["webly" "goldly"  "pinkie"
           "picasso"])

(def notebook ["notebook"
               "nrepl-middleware" "notebook-encoding" "gorilla-explore"
               "kernel-cljs-shadow"])
(def ui ["ui-repl"
         "ui-input"
         "ui-site"
         "ui-vega"
         "ui-highcharts"
         "ui-math"
         "ui-gorilla"
         "ui-quil"
         "ui-leaflet"
         "ui-cytoscape"
         "ui-code"
         "ui-markdown"
         "ui-binaryclock"])

; ^:R
(defn artefacts [name list]
  ^:R  ; this is needed, soartefacts function can be used in the repl
  [:div
   [:h1.text-3xl.text-blue-900 name]
   (into [:div]
         (map project list))])

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defmethod reagent-page :user/artefacts [{:keys [route-params query-params handler] :as route}]
  [:div.bg-green-100.h-screen
   [link-href "/" "goto main page"]
   [:h1.text-3xl.text-blue-900.mb-5.mt-5 "PinkGorilla Clojars Artefacts"]
   [:audio {:src "/r/demo/daddys-outta-town.mp3" 
            :controls false
            :auto-play true
            :loop true
            :preload "auto"
            }]
   [artefacts "misc" main]
   [artefacts "ui extensions" ui]
   [artefacts "notebook" notebook]])