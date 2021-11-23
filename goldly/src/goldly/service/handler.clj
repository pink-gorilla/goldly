(ns goldly.service.handler
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [ring.util.response :as res]
   [webly.web.middleware :refer [wrap-api-handler]]
   [webly.web.handler :refer [add-ring-handler]]
   [goldly.service.core :refer [run-service]]))

(defn service-handler
  [req]
  (info "service-api-handler: " req)
  (let [body-params (:body-params req)
        args (select-keys body-params [:fun :args])
        _ (info "service: "  args)
        response (run-service args)]
    (if (:error response)
      (res/bad-request response)
      (res/response response))))

(add-ring-handler :goldly/service (wrap-api-handler service-handler))
