(ns goldly.cljs.watch
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [hawk.core :as hawk]
   [modular.ws.core :refer [send! send-all! send-response]]
   [goldly.cljs.explore :refer [load-file!]]))

(defn to-canonical [path]
  (->>
   (clojure.java.io/file path)
   (.getCanonicalFile)
   (.getPath))
  path)

(defn broadcast-file [event-name root file]
  (infof "broadcasting root: %s file: %s" root file)
  (let [;p (.getPath file)
        name (.getName file)
        result (load-file! (str root "/" name))]
    (send-all! [event-name result])))

(defn process-file-change [event-name root
                           ctx {:keys [kind file] :as e}]
  ;(info "watcher-action: event: " e) 
  ; {:kind :delete, 
  ;  :file #object[java.io.File 0x7ca1018 "/home/andreas/pinkgorilla/goldly/profiles/demo/cljs-sci/test.cljs"]}

  ;(info "watcher-action: context: " ctx) {}
  ;(info "root: " root)
  (case kind
    :create (broadcast-file event-name root file)
    :modify (broadcast-file event-name root file)
    :delete (info "file deleted: " root file))
  ctx)

(defn watch [path event-name]
  (assert (string? path))
  (let [dir (io/file path)
        root (to-canonical path)
        watch-paths [path]]
    (when (.exists dir)
      (info "watching: " dir)
      (hawk/watch! {:watcher :polling}
                   [{:paths watch-paths
                     :handler (partial process-file-change event-name root)}]))))
