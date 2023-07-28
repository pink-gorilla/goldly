(ns demo.notebook.nbcollection
  (:require
   [modular.config :as config]
   [reval.document.notebook :refer [eval-notebook]]
   [reval.document.collection :refer [nb-collections]]))

(comment
  (config/set!
   :reval
   {:rdocument {:storage-root "/tmp/rdocument/"
                :url-root "/api/rdocument/file/"}
    :collections {:demo [:clj "demo/notebook/"]
                  :user [:clj "notebook/big_list"]}})
;
  )

(nb-collections)
