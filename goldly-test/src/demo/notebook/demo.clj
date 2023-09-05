(ns demo.notebook
  (:require
   [reval.document.collection :as r]
   [modular.config :refer [get-in-config]]))

;; this can be modified!
;; yes! or no ???

(r/nb-collections)

(get-in-config [:reval :collections])

(get-nss-list :clj ["demo/notebook"])

(require '[scratchpad.handler])

(resolve 'scratchpad.handler/wrapped-scratchpad-set-handler)

(resolve 'scratchpad.handler/wrapped-scratchpad-get-handler)

(def get-handler 
  (resolve 'scratchpad.handler/wrapped-scratchpad-get-handler)
  )

(get-handler {})

(def get-ns-list 
(resolve 'reval.document-handler/wrapped-get-ns-list)  
  )

(-> (get-ns-list {})
    :body
    slurp
 
 )








