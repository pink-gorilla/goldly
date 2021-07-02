(ns goldly.store.watch
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [hawk.core :as hawk]
   [webly.ws.core :refer [send! send-all! send-response]]
   [goldly.store.file :refer [cljs-load]]))

(defn to-canonical [path]
  (->>
   (clojure.java.io/file path)
   (.getCanonicalFile)
   (.getPath))
  path)

(defn broadcast-file [file]
  (info "broadcasting cljs-sci file: " file)
  (let [;p (.getPath file)
        name (.getName file)
        result (cljs-load name)]
    (send-all! [:goldly/cljs-sci-reload result])))

(defn process-file-change [root ctx {:keys [kind file] :as e}]
  ;(info "watcher-action: event: " e) 
  ; {:kind :delete, 
  ;  :file #object[java.io.File 0x7ca1018 "/home/andreas/pinkgorilla/goldly/profiles/demo/cljs-sci/test.cljs"]}

  ;(info "watcher-action: context: " ctx) {}
  ;(info "root: " root)
  (case kind
    :create (broadcast-file file)
    :modify (broadcast-file file)
    :delete (info "sci-cljs deleted: " file))
  ctx)

(defn cljs-watch []
  (let [root (to-canonical "cljs-sci")
        watch-paths ["cljs-sci"]]

    (info "cljs-sci watch: " watch-paths)
    (hawk/watch! {:watcher :polling}
                 [{:paths watch-paths
                   :handler (partial process-file-change root)}])))



