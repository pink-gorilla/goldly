(ns goldly-server.routes
  (:require
   [bidi.bidi :as bidi]
   [goldly.routes]))

(def routes
  {:app (assoc goldly.routes/routes-app
               "" :goldly/about ; so / route goes also to :goldly/about
               "notebook-test" :notebook/test
               "bongo" (bidi/tag :goldly/system :hello-world)
               "bongo/" (bidi/tag :goldly/system :hello-world))
   :api
   goldly.routes/routes-api})