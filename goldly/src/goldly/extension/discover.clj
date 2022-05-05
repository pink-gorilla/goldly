(ns goldly.extension.discover
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [clojure.edn :as edn]
   [resauce.core :as rs]
   [modular.writer]
   [modular.config :refer [config-atom]]
   ; build time
   [goldly.extension.theme :refer [add-extension-theme set-lazy-themes!]]
   ;[goldly.extension.pinkie :refer [pinkie-atom save-pinkie]]
   [goldly.extension.cljs :refer [cljs-init add-extension-cljs]]
   ; runtime
   [goldly.extension.cljs-autoload :refer [add-extension-cljs-autoload]]
   [goldly.extension.clj :refer [add-extension-autoload-clj-ns]]
   [goldly.extension.core :refer [save-extensions ext-themes]]

   ; side effects
   [goldly.routes] ; side effects
   ))

(defn add-extension [{:keys [name] :as extension}]
  (debug "adding extension: " name)
   ; build-time
  (add-extension-theme extension)
  (add-extension-cljs extension)

  ;run-time
  (add-extension-cljs-autoload extension) ; the sci namespaces/files.
  (add-extension-autoload-clj-ns extension))

(defn discover-extensions []
  (let [r  (rs/resource-dir "ext")
        extensions (for [f r]
                     (-> f slurp edn/read-string))]
    (debug "discovered extensions: " (pr-str r))
    (cljs-init)

    ; discover
    (doall (for [ext extensions]
             (add-extension ext)))

    ; save collected data.
    (save-extensions extensions)
    ;(save-pinkie)
    ;(swap! config-atom assoc :pinkie @pinkie-atom)
    (set-lazy-themes! (ext-themes))))

(comment

  (rs/resources "demo.notebook.goldly")
  (rs/resources "")

  (-> (rs/resources "demo/notebook/apple.clj")
      first
      (rs/directory?))

  (-> (rs/resource-dir "ext")
      ;first
      last
      ;slurp
      )

  (-> (rs/resource-dir "demo/notebook")
      ;first
      last
      rs/name
      ;slurp
      )

  ;(recursive-resource-paths "ext")
  ;(recursive-resource-paths "")

;  
  )