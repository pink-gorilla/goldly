(ns goldly.run.cljs-load
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [modular.ws.core :refer [send! send-all! send-response]]
   [modular.config :refer [get-in-config]]
   [modular.resource.explore  :as resources]
   [modular.file.explore :as filesystem]
   ;[modular.file.watch :refer [watch]]
   [goldly.run.watch :refer [watch]]
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

;; WATCH

(defn load-file! [filename]
  (let [code (slurp filename)]
    {:filename filename
     :code code}))

(defn on-cljs-file-change [{:keys [root kind file file-full]}]
  (infof "on-cljs-file-change root: %s file: %s" root file)
  (when (and
         (or (= kind :create) (= kind :modify))
         (str/ends-with? file "cljs")
         (not (str/starts-with? file ".")) ; emacs temp files.
         )
    (let [result (load-file! file-full)]
      (info "triggering hot-reload for: " file-full)
      (send-all! [:goldly/cljs-sci-reload result]))))

(defn watch-dir [dir]
  (warn "watching cljs files in dir: " dir)
  (watch dir on-cljs-file-change))

(defn start-cljs-watch [dir]
  (info "starting developer mode cljs-watch in dir: " dir)
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

