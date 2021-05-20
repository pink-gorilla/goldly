(ns goldly.api.handler
  (:require
   [webly.web.middleware :refer [wrap-api-handler]]
   [webly.web.handler :refer [add-ring-handler]]
   [goldly.api.scratchpad :refer [scratchpad-get-handler scratchpad-set-handler]]))

(add-ring-handler :goldly/scratchpad-get (wrap-api-handler scratchpad-get-handler))
(add-ring-handler :goldly/scratchpad-set (wrap-api-handler scratchpad-set-handler))
