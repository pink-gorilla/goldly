(ns goldly.web.middleware-transit
  (:require
   [cognitect.transit :as transit]
   [luminus-transit.time :as time]
   [muuntaja.core :as m]))


; from clojurewb.

(def instance
  (m/create
   (-> m/default-options
       (update-in
        [:formats "application/transit+json" :decoder-opts]
        (partial merge time/time-deserialization-handlers))
       (update-in
        [:formats "application/transit+json" :encoder-opts]
        (partial merge time/time-serialization-handlers)))))