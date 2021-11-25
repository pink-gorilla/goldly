(ns goldly.cljs.discover
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [modular.config :refer [get-in-config]]
   [modular.writer :refer [write-target write-status]]
   [modular.resource.explore  :as resources]
   [modular.file.explore :as filesystem]
   [modular.file.watch :refer [watch]]
   [goldly.cljs.loader :refer [load-file-or-res!]]
   [goldly.extension.cljs-autoload :refer [autoload-cljs-res-a]]
   [goldly.service.core :as s]))

;; EXPLORE ONCE

(defn autoload-dir []
  (get-in-config [:goldly :autoload-cljs-dir]))

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
  (info "explore sci-autoload-cljs dir: " dir)
  (->> (resources/describe-files dir)
       (map #(:name-full %)) ; str dir "/" %
       vec))

(defn cljs-explore []
  (process-dir-dirs
   explore-once
   #(warn "no cljs [:goldly :autoload-dir] defined!")))

(defn cljs-explore-with-res []
  (-> (concat @autoload-cljs-res-a (cljs-explore))
      vec))

;; WATCH

(defn watch-dir [dir]
  (watch dir :goldly/cljs-sci-reload))

(defn cljs-watch []
  (process-dir-dirs
   watch-dir
   nil))

;; SERVICES

(s/add {:cljs/explore cljs-explore-with-res ; cljs-explore
        :cljs/load load-file-or-res!})

(comment
  (apply concat '(["helper.cljs"] ["error.cljs" "main.cljs" "info.cljs"]))

  ; test different configs:
  (swap! modular.config/config-atom
         #(assoc-in % [:goldly :autoload-dir] "src/demo/cljs"))
  (swap! modular.config/config-atom
         #(assoc-in % [:goldly :autoload-dir] ["src/demo/cljs-libs" "src/demo/cljs"]))

  (autoload-dir)

  (explore-once "src/demo/cljs")
  (explore-once "src/demo/cljs-libs")
  (cljs-explore)
  (cljs-watch)
  (cljs-explore-with-res)

;  
  )

