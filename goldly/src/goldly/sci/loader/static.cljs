(ns goldly.sci.loader.static
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [clojure.string :refer [last-index-of]]
   [cemerick.url :as curl]))

(defn application-url []
  (-> js/window .-location .-href))

(defn dynamic-base []
  (let [url (application-url)
        url-base (subs url 0 (last-index-of url "/"))]
    (info "dynamic-base: " url-base)
    url-base))
