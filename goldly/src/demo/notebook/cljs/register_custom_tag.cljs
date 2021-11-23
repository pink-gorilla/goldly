



(defn bongo []
  [:div
   [:h1 "bongo"]
   [:p "trott"]])

; register the tag, and ONLY show the definiton of the new renderer
(register-tag :p/bongo bongo)

; render custom tag from cljs
^:R [:p/bongo]

; change theme via sci.
(rf/dispatch [:css/set-theme-component :codemirror "mdn-like"])