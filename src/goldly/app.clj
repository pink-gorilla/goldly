(ns goldly.app
  "the main goldly application"
  (:require
   [clojure.java.io]
   [taoensso.timbre :as timbre :refer [info warn error]]
   [webly.config :refer [get-in-config]]
   ;[goldly.runner.clj-fn] ; side-efects
   [goldly.notebook.picasso] ; side-efects
   [goldly.puppet.require :refer [require-namespaces]]
   [goldly.sci.bindings :refer [generate-bindings]]
   [goldly.ws]
   [goldly.api.handler]
   [goldly.discover :as d]
   [goldly.service.core]
   [goldly.broadcast.core]))

(defn goldly-compile! []
  (let [{:keys [systems extensions]}
        (get-in-config [:goldly])]
    ; extensions can add to cljs namespaces. therefore extensions have to
    ; be included at compile time.
    (d/discover)
    (if extensions
      (do (info "loading extensions from ns: " extensions)
          (require-namespaces extensions))
      (warn "no goldly extensions defined!"))
    (generate-bindings)))

(defn goldly-run! []
  (let [{:keys [systems extensions]}
        (get-in-config [:goldly])]
    (if systems
      (do (info "loading systems from ns: " systems)
          (require-namespaces systems))
      (warn "no goldly systems defined!"))
    (if extensions
      (do (info "loading extensions from ns: " extensions)
          (require-namespaces extensions))
      (warn "no goldly extensions defined!"))))


