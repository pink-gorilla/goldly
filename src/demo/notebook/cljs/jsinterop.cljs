

;; working

#js [1 2 3]

(-> (j/get js :console)
    (j/call :log 34))

(j/get js :window)

(j/get js :Error)

(-> (j/get js :Number)
    (j/get :NaN))

;; not working

js/Error  ; could not resolve symbol

js/Math.PI

(.log js/console "hi") ; not allowed

(.length "foo")

(js/console.log "42")

(some? js/Number.POSITIVE_INFINITY)

(. js/goog -LOCALE)

(.toString js/Math.PI)

(js/parseInt (.-message (js/Error. \"42 \")))

(def err js/Error) (js/parseInt (.-message (err. \"42 \")))

(js/parseInt (.-message (js/Error. \"42 \")))

(js/parseInt "42 ")

(String/fromCharCode 67)