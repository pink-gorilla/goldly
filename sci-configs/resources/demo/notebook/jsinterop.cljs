(ns demo.notebook.jsinterop
  (:require
   [applied-science.js-interop :as j]))

#js [1 2 3]

js/Math.PI
(.toString js/Math.PI)

(js/console.log 42)
(.log js/console "hi")

(. js/goog -LOCALE)
(js/parseInt "42")

js/Error  ; could not resolve symbol
(js/parseInt (.-message (js/Error. "42 ")))

(some? js/Number.POSITIVE_INFINITY)

;; using J

(-> (j/get js2 :console)
    (j/call :log 34))

(j/get js :window)

(j/get js :Error)

(-> (j/get js :Number)
    (j/get :NaN))

;; not working

(.length "foo")

(def err js/Error) (js/parseInt (.-message (err. \"42 \")))

(String/fromCharCode 67)