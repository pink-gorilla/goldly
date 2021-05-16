(ns goldly.app
  "the main goldly application"
  (:require
   [clojure.java.io]
   [taoensso.timbre :as timbre :refer [info warn]]
   [webly.config :refer [get-in-config]]
   [goldly.runner.clj-fn] ; side-efects
   [goldly.notebook.picasso] ; side-efects
   [goldly.puppet.require :refer [require-namespaces]]
   [goldly.sci.bindings :refer [add-cljs-namespace add-cljs-bindings generate-bindings]]))

(add-cljs-namespace [clojure.walk :as walk])
(add-cljs-namespace [goldly.sci.bindings-goldly])
(add-cljs-namespace [goldly.code.core])
(add-cljs-bindings {'sin goldly.sci.bindings-goldly/sin
                    'println println})

(defn goldly-compile! []
  (let [{:keys [systems extensions]}
        (get-in-config [:goldly])]
    ; extensions can add to cljs namespaces. therefore extensions have to
    ; be included at compile time.
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


