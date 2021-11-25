(ns goldly.scratchpad-handler
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [clojure.core.async :refer [go <! <!!]]
   [ring.util.response :as res]
   [ring.util.request :refer  [body-string]]
   [modular.webserver.middleware.api :refer [wrap-api-handler]]
   [modular.webserver.handler.registry :refer [add-ring-handler]]
   [goldly.scratchpad :as scratchpad]))

(defn scratchpad-get-handler
  [req]
  (debug "scratchpad-get-handler: " req)
  (scratchpad/get!)
  (<!! (go  (let [s (<! scratchpad/chan-scratchpad-get)]
              (res/response s))))
   ;(res/bad-request {:error 123})
  )

(defn scratchpad-set-handler
  [req]
  (debug "snippet-api-handler: " req)
  (let [src (body-string req)]
    (info "src:" src)
    (scratchpad/show! src)
    (res/response {:message "src sent to scratchpad."})
    #_(res/bad-request save-result)))

(add-ring-handler :scratchpad/get (wrap-api-handler scratchpad-get-handler))
(add-ring-handler :scratchpad/set (wrap-api-handler scratchpad-set-handler))
