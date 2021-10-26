(ns goldly.cljs.loader
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [webly.config :refer [get-in-config]]
   [goldly.service.core :as s]
   [notebook.explore.explore :refer [explore-dir load-file!]]
   [notebook.explore.watch :refer [watch]]))

(defn autoload-dir []
  (get-in-config [:goldly :autoload-dir]))

(defn process-dir-dirs
  [fun fun-err]
  (let [dir (autoload-dir)]
    (assert (or (nil? dir)
                (vector? dir)
                (string? dir)))
    (if dir
      (if (vector? dir)
        (->> (map fun dir)
             (apply concat)
             vec)
        (fun dir))
      (when fun-err
        (fun-err dir)))))

(defn explore-once [dir]
  (info "loading cljs from: " dir)
  (->> (explore-dir dir "autoload-dir")
       (map #(str dir "/" %))
       vec))

(defn cljs-explore []
  (process-dir-dirs
   explore-once
   #(warn "no cljs [:goldly :autoload-dir] defined!")))

(defn watch-dir [dir]
  (watch dir :goldly/cljs-sci-reload))

(defn cljs-watch []
  (process-dir-dirs
   watch-dir
   nil))

(defn cljs-load [filename]
  (assert (string? filename))
  (info "loading cljs file: " filename)
  (load-file! filename))

(s/add {:cljs/explore cljs-explore
        :cljs/load cljs-load})

(comment
  (apply concat '(["helper.cljs"] ["error.cljs" "main.cljs" "info.cljs"]))

  ; test different configs:
  (swap! webly.config/config-atom
         #(assoc-in % [:goldly :autoload-dir] "src/demo/cljs"))
  (swap! webly.config/config-atom
         #(assoc-in % [:goldly :autoload-dir] ["src/demo/cljs-libs" "src/demo/cljs"]))
  (autoload-dir)

  (explore-once "src/demo/cljs")
  (explore-once "src/demo/cljs-libs")
  (cljs-explore)
  (cljs-watch)

;  
  )

