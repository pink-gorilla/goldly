

^:R [:p/text "h2\nkllj\njj"]

;  from a clojurescript kernel, we can generate new renders easily.
; custom renderer definition

(defn bongo []
  [:div
   [:h1 "bongo"]
   [:p "trott"]])

; register the tag, and ONLY show the definiton of the new renderer
(register-tag :p/bongo bongo)

; render custom tag from cljs
^:R [:p/bongo]
