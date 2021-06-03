(ns goldly-server.routes
  (:require
   [goldly.routes]))

(def routes-app
  (assoc goldly.routes/routes-app
         "" :goldly/about ; so / route goes also to :goldly/about
         "notebook-test" :notebook/test))

(def routes-api
  goldly.routes/routes-api)