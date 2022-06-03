(ns goldly.devtools.page.help
  (:require
   [goog.string :refer [format]]
   [goldly.devtools.ui-helper :refer [add-page-template h1]]))

; artefacts

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

(def apps-and-demo ["studio"
                    "goldly-docs"
                    "pages"
                    "demo-goldly"])

(def build-tool ["modular" "webly" "goldly"])

(def goldly-extensions ["reval"
                        "ui-repl"
                        "ui-input"
                        "ui-site"
                        "ui-vega"
                        "ui-highcharts"
                        "ui-math"
                        "ui-gorilla"
                        "ui-quil"
                        "ui-leaflet"
                        "ui-cytoscape"
                        "ui-codemirror"
                        "ui-markdown"
                        "ui-binaryclock"])

(def notebook-legacy ["picasso"
                      "pinkie"
                      "notebook"
                      "nrepl-middleware" "notebook-encoding" "gorilla-explore"
                      "kernel-cljs-shadow"])

(defn artefacts [name list]
  ^:R  ; this is needed, soartefacts function can be used in the repl
  [:div
   [:h1.text-3xl.text-red-400 name]
   (into [:div]
         (map project list))])

(defn devtools-page [{:keys [route-params query-params handler] :as route}]
  [:div ; .w-screen.h-screen
   [h1 "goldly devtools"]
   [:div.mb-5]
   ; this mp3 is too big for clojars
   ;[:audio {:src "/r/daddys-outta-town.mp3"
   ;         :controls false
   ;         :auto-play true
   ;         :loop true
   ;         :preload "auto"}]
   [h1 "What is goldly"]
   [:ul
    [:li "Can run clj code in the browser. This is done via sci interpreter."]
    [:li "Via hiccup-fh (functional hiccup) new render functions can be executed from clj."]
    [:li (str "The goldly extension manager will compile your favorite hiccup-fn functions "
              "into a precompiled js bundle that is served with goldly")]]

   [h1 "artefacts"]
   [artefacts "apps and demo" apps-and-demo]
   [artefacts "build tools" build-tool]
   [artefacts "goldly extensions" goldly-extensions]
   [artefacts "notebook (legacy)" notebook-legacy]])

(add-page-template devtools-page :devtools)

