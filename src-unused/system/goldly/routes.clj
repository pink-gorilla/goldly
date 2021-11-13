(ns goldly-server.routes
  (:require
   [bidi.bidi :as bidi]
   [goldly.routes]))


(ns goldly.routes)

(def routes-app
  {"goldly/systems" :goldly/system-list
   ["system/" :system-id "/" :system-ext] :goldly/system-ext
   ["system/" :system-id] :goldly/system})

(def routes-api
  {"service" {:post :goldly/service}
   "scratchpad" {:get  :goldly/scratchpad-get
                 :post :goldly/scratchpad-set}})

(def routes
  {:app (assoc goldly.routes/routes-app
               "" :goldly/no-app
               "goldly/about" :goldly/about ; so / route goes also to :goldly/about
               "goldly/status" :goldly/status
               "goldly/theme" :goldly/theme
               "goldly/reload" :goldly/reload-cljs
               "goldly/notebooks" :goldly/notebooks
               "goldly/nrepl" :nrepl/info
               "repl" :goldly/repl
               ;"bongo" (bidi/tag :goldly/system :hello-user)
               ;"bongo/" (bidi/tag :goldly/system :hello-user)
               )
   :api goldly.routes/routes-api})

