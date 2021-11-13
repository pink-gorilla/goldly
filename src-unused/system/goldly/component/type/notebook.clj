(ns goldly.component.type.notebook
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [clojure.java.io :as io]
   [modular.config :refer [get-in-config]]
   [notebook.rewriteclj.parser :refer [file->notebook]]
   [goldly.explore.explore :refer [explore-dir]]
   [goldly.explore.watch :refer [watch]]
   [goldly.component.load :refer [load-index load-component]]))

;; index

(defn exists? [filename]
  (-> (io/file filename) .exists))
(defn dir? [filename]
  (-> (io/file filename) .isDirectory))

(defn explore-dir-map [dir]
  (let [coll-name (-> (io/file dir) .getName)
        nb-collection (->> (explore-dir dir "notebook-dir")
                           (map #(assoc {:type :clj}
                                        :name %
                                        :id (str dir "/" %)))
                           (into [])
                           (assoc {:name coll-name} :notebooks))]
    (debugf "dir %s: nb collection: %s " dir nb-collection)
    nb-collection))

(defn notebook-explore []
  (let [dir (get-in-config [:goldly :notebook-dir])]
    (cond
      (string? dir)
      [(explore-dir-map dir)]

      (or (seq? dir) (vector? dir))
      (->> (map explore-dir-map dir)
           (into []))

      (nil? dir)
      (warn "goldly [notebook-dir] setting is nil. not exploring for notebooks.")

      :else
      (error "goldly [notebook-dir] setting must be a string or a seq."))))

(defn notebook-watch []
  (let [dir  (get-in-config [:goldly :notebook-dir])]
    (cond
      (string? dir)
      (watch dir :goldly/notebook-reload)

      (or (seq? dir) (vector? dir))
      (doall (map #(watch % :goldly/notebook-reload) dir))

      (nil? dir)
      (debug "goldly [notebook-dir] setting is nil. not exploring for notebooks.")

      :else
      (debug "goldly [notebook-dir] setting must be a string or a seq."))))

(defmethod load-index :notebook [{:keys [type]}]
  (let [nb-collections (notebook-explore)]
    (warn "notebook collections: " nb-collections)
    nb-collections))

;; component

(defn notebook-load [filename-full]
  (assert (string? filename-full))
  (assert (exists? filename-full))
  (let [nb (file->notebook filename-full)]
    (warn "notebook: " nb)
    nb))

(defmethod load-component :notebook [{:keys [id type]}]
  (let [data (notebook-load id)]
    (if data
      {:id id :data data}
      {:id id :error "not found"})))