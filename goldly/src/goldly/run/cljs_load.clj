(ns goldly.run.cljs-load
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [modular.config :refer [get-in-config]]
   [modular.resource.explore  :as resources]
   [modular.file.explore :as filesystem]
   [modular.file.watch :refer [watch]]
   [goldly.cljs.loader :refer [load-file-or-res!]]
   [goldly.service.core :as s]))

;; alternative implementation

(defn process-dir-dirs
  [dir fun fun-err]
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
      (fun-err dir))))

;; EXPLORE RESOURCES

(defn explore-once [dir]
  (info "explore sci-autoload-cljs dir: " dir)
  (->> (resources/describe-files dir)
       (map #(:name-full %)) ; str dir "/" %
       vec))

;; CONFIG

(defn autoload-cljs-dir []
  (get-in-config))

(defn cljs-explore [dir]
  (process-dir-dirs
   dir
   explore-once
   #(warn "no cljs [:goldly :autoload-dir] defined!")))

(defn cljs-files [ext-dirs goldly-autoload]
  (-> (concat ext-dirs (cljs-explore goldly-autoload))
      vec))

;; SERVICES

(defn add-cljs-file-services [ext-dirs goldly-autoload]
  (let [cljs-explore-fn #(cljs-files ext-dirs goldly-autoload)]
    (s/add {:cljs/load load-file-or-res!
            :cljs/explore cljs-explore-fn})))

;; WATCH

(defn watch-dir [dir]
  (watch dir :goldly/cljs-sci-reload))

(defn start-cljs-watch [dir]
  (process-dir-dirs
   dir
   watch-dir
   nil))

(comment

  ;(autoload-dir)
; (cljs-explore)
;  (cljs-watch)
;  (cljs-explore-with-res)

;  
  )

