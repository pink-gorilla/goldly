(ns goldly.routes)

(def routes-app
  {"about"  :goldly/about
   "goldly" :goldly/system-list
   ["system/" :system-id] :goldly/system})

(def routes-api
  {})




