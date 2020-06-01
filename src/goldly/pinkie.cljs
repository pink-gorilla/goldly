(ns goldly.pinkie
  (:require
   [pinkgorilla.ui.error :refer [error-boundary]]
   [pinkgorilla.ui.pinkie :refer [tag-inject convert-style-as-strings-to-map convert-render-as register-tag]]
   [pinkgorilla.ui.widget :refer [resolve-widget]]))

(defn reagent-inject [{:keys [map-keywords widget]} component]
  (let [;_ (info "map-keywords: " map-keywords "widget: " widget " reagent component: " component)
        ;_ (info "meta data: " (meta component))
        component (convert-render-as component)
        ;_ (println "after convert-render-as " component)
        component (if map-keywords (tag-inject component) component)
        component (if widget (resolve-widget component) component)
        component (if map-keywords (convert-style-as-strings-to-map component) component)
        ;_ (info "inject result: " component)
        ]
    [:div.reagent-output component]))

(defn pinkie-render-unsafe
  [output]
  (let [{:keys [hiccup map-keywords widget]} (:content output)]
    (reagent-inject {:map-keywords map-keywords :widget widget} hiccup)))

(defn pinkie-render [output]
  [error-boundary
   [pinkie-render-unsafe output]])

(register-tag :p/pinkie pinkie-render)