(ns goldly.app.build
  (:require
   [clojure.string] ; side effects
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.config :refer [get-in-config config-atom load-config! add-config resolve-config-key]]
   [modular.require :refer [require-namespaces]]
   [webly.build.profile :refer [setup-profile server?]]
   [webly.build.core :refer [build]]
   [goldly.extension.discover :refer [discover-extensions]]
   [goldly.sci.bindings :refer [generate-bindings]]
   [goldly.static :refer [export-sci-cljs]]
   [goldly.cljs.discover :refer [generate-cljs-autoload]]))

(defn extensions->webly-config []
  (info "extensions->webly-config")
  (generate-bindings)
  (generate-cljs-autoload)
  (export-sci-cljs))

(defn goldly-build [{:keys [config profile]}]
  (warn "loading config: " config)
  (load-config! config)
  (warn "requiring namespaces..") ; does this have to be AFTER discovering extensions? in run- for sure yes.
  (require-namespaces (get-in-config [:ns-clj]))
  (warn "resolving [:webly :routes]")
  (resolve-config-key config [:webly :routes])
  (warn "discovering extensions..")
  (discover-extensions)

; extensions can add to cljs namespaces. therefore extensions have to
  ; be included at compile time. But extensions also are needed
  ; for css and clj ns. 
  (let [profile (setup-profile profile)]
    (when (:bundle profile)
      (extensions->webly-config)
      (build profile))))