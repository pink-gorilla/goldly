(ns goldly.routes)


(def routes
  {:app {"" :goldly/no-page
         "goldly/reload" :goldly/reload-cljs}
   :api {}})
