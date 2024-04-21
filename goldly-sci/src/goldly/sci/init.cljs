(ns goldly.sci.init
  (:require
   [taoensso.timbre :as timbre :refer [info warn]]
   [webly.spa.resolve :refer [set-resolver!]]
   [goldly.sci :refer [requiring-resolve]]))

(defn set-goldly-resolver []
  (info "setting webly.spa.resolver to sci-requiring resolve..")
  (set-resolver! requiring-resolve))

 