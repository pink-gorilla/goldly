(ns goldly.run.watch
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [hawk.core :as hawk]))

;; this needs to be moved to modular.file.watch.

(defn- to-canonical [path]
  (->>
   (clojure.java.io/file path)
   (.getCanonicalFile)
   (.getPath))
  path)

(defn- process-file-change [cb-fn root
                            ctx {:keys [kind file] :as e}]
  ;(warn "change root: " root) ; always root, even for sub-dirs
  ;(warn "change ctx: " ctx) ; always empty
  ; {:kind :delete, 
  ;  :file #object[java.io.File 0x7ca1018 "/home/andreas/pinkgorilla/goldly/profiles/demo/cljs-sci/test.cljs"]}
  (cb-fn {:root root
          :kind kind
          :file (.getName file)
          :file-full (.getPath file)})
  ctx)

(defn watch [path cb-fn]
  (assert (string? path))
  (let [dir (io/file path)
        root (to-canonical path)
        watch-paths [path]]
    (when (.exists dir)
      (info "watching: " dir)
      (hawk/watch! {:watcher :polling}
                   [{:paths watch-paths
                     :handler (partial process-file-change cb-fn root)}]))))

