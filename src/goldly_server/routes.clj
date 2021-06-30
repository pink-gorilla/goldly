(ns goldly-server.routes
  (:require
   [bidi.bidi :as bidi]
   [goldly.routes]))

(def routes
  {:app (assoc goldly.routes/routes-app
               "" :goldly/no-app
               "goldly/about" :goldly/about ; so / route goes also to :goldly/about
               "repl" :goldly/repl
               "bongo" (bidi/tag :goldly/system :hello-user)
               "bongo/" (bidi/tag :goldly/system :hello-user))
   :api goldly.routes/routes-api})

