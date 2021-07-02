(ns goldly.routes)

(def routes-app
  {"goldly/systems" :goldly/system-list
   ["system/" :system-id "/" :system-ext] :goldly/system-ext
   ["system/" :system-id] :goldly/system})

(def routes-api
  {"scratchpad" {:get  :goldly/scratchpad-get
                 :post :goldly/scratchpad-set}})




