(ns goldly.devtools
  (:require
   [taoensso.timbre  :refer [debug info warn error]]
   [modular.config :as config :refer [get-in-config]]
   [reval.document.collection :as nbcol]
   [reval.document.notebook :refer [load-notebook eval-notebook save-notebook]]
   [reval.default] ; side effects
   [goldly.service.core :as s]
   [goldly.document-handler] ; side effect
   [goldly.scratchpad-handler] ; side effect
   ))

(info "goldly devtools loading..")

(def default-reval-config
  {:rdocument  {:storage-root "/tmp/rdocument/"
                :url-root "/api/rdocument/file/"}
   :collections {:demo [:clj "demo/notebook/"]}})

(defn get-config []
  (let [user (get-in-config [:reval])
        user-rdocument (:rdocument user)
        user-collections (:collections user)]
    (if user
      {:rdocument (if user-rdocument
                    user-rdocument
                    (:rdocument default-reval-config))
       :collections (if user-collections
                      user-collections
                      (:collections default-reval-config))}
      (do (warn "no :reval key in config. using default reval settings")
          default-reval-config))))

(config/set! :reval (get-config))

(defn nb-collections []
  (nbcol/get-collections (get-in-config [:reval :collections])))

(s/add {:nb/collections nb-collections
        :nb/load  load-notebook
        :nb/eval  eval-notebook
        :nb/save save-notebook})

(comment
  devtools-config
  (nb-collections)

;  
  )