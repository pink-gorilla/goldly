(ns demo.routes
  (:require
   [goldly.routes]))

(def routes-app
  (assoc goldly.routes/routes-app
         "demo" :demo/main))

(def routes-api
  goldly.routes/routes-api)