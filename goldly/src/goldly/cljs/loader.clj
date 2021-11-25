(ns goldly.cljs.loader
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [modular.resource.load :refer [slurp-res]]))

(defn load-file! [filename]
  (assert (string? filename))
  (let [code (slurp filename)]
    {:filename filename
     :code code}))

(defn load-file-or-res! [filename]
  (assert (string? filename))
  (let [code (slurp-res filename)]
    {:filename filename
     :code code}))

(comment
  (load-file-or-res! "goldly/lib/util.cljs")
  (load-file-or-res! "src/demo/page/info.cljs")
;  
  )