(ns goldly.api.scratchpad
  (:require
   [clojure.core.async :refer [go <! <!!]]
   [taoensso.timbre :refer [trace debug info error]]
   [ring.util.response :as res]
   [ring.util.request :refer  [body-string]]
   [goldly.scratchpad.core :as ws]))

(defn scratchpad-get-handler
  [req]
  (debug "scratchpad-get-handler: " req)
  (ws/scratchpad-get)
  (<!! (go  (let [s (<! ws/chan-scratchpad-get)]
              (res/response s))))
   ;(res/bad-request {:error 123})
  )

(defn scratchpad-set-handler
  [req]
  (debug "snippet-api-handler: " req)
  (let [src (body-string req)]
    (info "src:" src)
    (ws/scratchpad-set :pinkie src)
    (res/response {:message "src sent to scratchpad."})
    #_(res/bad-request save-result)))

