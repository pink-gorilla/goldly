(ns demo.notebook.applied-science-jsinterop
  (:require
   [applied-science.js-interop :as j]))

(def o #js {"a" 44 "b" "B"})

;; Read
(j/get o :x)
(j/get o "a")

(j/get-in o [:a])
(j/select-keys o [:a :b :c])

; this does not work:
;(j/get o .-x "fallback-value")