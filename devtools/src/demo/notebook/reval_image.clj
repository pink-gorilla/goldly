(ns demo.notebook.reval-image
  (:require
   [clojure.java.io :as io]
   [modular.persist.protocol :refer [loadr]]
   [reval.type.protocol :refer [to-hiccup]]
   [reval.ui :refer [img-inline img]]))

;; this notebook comes from pink.gorilla/reval

;; this notebook shows how you buffered images in clj
;; can be rendererd to a reproducible notebook.
;; load-img is just a helper function

(-> "demo/public/sun.png"
    io/file
    .exists)

(-> (loadr :png "demo/public/sun.png")
    img-inline
    ;to-hiccup
    )
(-> (loadr :png "demo/public/sun.png")
    img
    (assoc :alt "adf")
    ;type
    ;to-hiccup
    )


