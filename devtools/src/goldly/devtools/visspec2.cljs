(ns goldly.devtools.visspec2
  (:require
   [viz :refer [show]]
   [goldly.sci :refer [resolve-symbol-sci]]))

(defn safe-resolve2 [s]
  (try
    (resolve-symbol-sci s)
    (catch :default e
      (println "renderer not found: " s)
      nil)))

(defn render-vizspec2 [h]
  ;(println "rendering vizspec: " h)
  ;(println "first item in vec:" (first h) "type: " (type (first h)))
  ;(println "render fn:" (get-symbol-value (first h)))
  ;(println "now showing..")
  (let [h-fn (show safe-resolve2 h)]
    ;(println "rendered spec: " (pr-str h-fn))
    h-fn))