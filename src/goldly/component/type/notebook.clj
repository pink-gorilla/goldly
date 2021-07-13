(ns goldly.component.type.notebook
  (:require
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [rewrite-clj.parser :as p]
   [rewrite-clj.node :as n]
   [picasso.id :refer [guuid]]
   [goldly.file.explore :refer [explore-dir load-file!]]
   [goldly.file.watch :refer [watch]]
   [goldly.component.load :refer [load-index load-component]]))

(def notebook-dir "goldly/notebooks")

;; index

(defn notebook-explore []
  (explore-dir notebook-dir))

(defn notebook-watch []
  (watch notebook-dir :goldly/notebook-reload))

(defmethod load-index :notebook [{:keys [type]}]
  {:notebooks (notebook-explore)})

;; component

(defn text->forms [s]
  (info "text: " s)
  (let [top-forms (->> s
                       (p/parse-string-all)
                       :children
                       (filter #(= (:tag %) :list))
                       (map n/string))]
    (warn "top forms: " top-forms)
    top-forms))

(comment
  (text->forms (slurp "goldly/notebooks/bananas.clj")))

(def nb-empty
  {:segments []})

(defn ->segment [code]
  {:id (guuid)
   :type :code
   :data {:kernel :clj
          :code code}})

(defn text->notebook [id s]
  (let [forms (text->forms s)]
    (-> nb-empty
        (assoc :meta {:id (guuid)
                      :title (name id)
                      :tags #{:clj}})
        (assoc :segments
               (into [] (map ->segment forms))))))

(defn notebook-load [filename]
  (let [s (load-file! notebook-dir filename)]
    (text->notebook filename (:code s))))

(comment
  (notebook-load "bananas.clj"))

(defmethod load-component :notebook [{:keys [id type]}]
  (let [filename (name id)
        data (notebook-load filename)]
    (if data
      {:id id :data data}
      {:id id :error "not found"})))