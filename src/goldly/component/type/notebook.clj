(ns goldly.component.type.notebook
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer [trace debug debugf info infof warn warnf error errorf]]
   [rewrite-clj.parser :as p]
   [rewrite-clj.node :as n]
   [picasso.id :refer [guuid]]
   [webly.config :refer [get-in-config]]
   [goldly.file.explore :refer [explore-dir load-file!]]
   [goldly.file.watch :refer [watch]]
   [goldly.component.load :refer [load-index load-component]]))

;; index

(defn notebook-explore []
  (let [dir  (get-in-config [:goldly :notebook-dir])]
    (explore-dir dir "notebook-dir")))

(defn notebook-watch []
  (let [dir  (get-in-config [:goldly :notebook-dir])]
    (watch dir :goldly/notebook-reload)))

(defmethod load-index :notebook [{:keys [type]}]
  {:notebooks (notebook-explore)})

;; component

(defn ->segment-code [kernel code]
  {:id (guuid)
   :type :code
   :data {:kernel kernel
          :code code}})

(defn ->segment-md [md]
  {:id (guuid)
   :type :md
   :data md})

(defn add-partial-comment [{:keys [code-partial] :as state} comment]
  (let [new-partial (str (or code-partial "")
                         comment
                         "\n")]
    (assoc state :code-partial new-partial)))

(defn add-partial-md [{:keys [md-partial] :as state} comment]
  (if (str/blank? comment)
    state
    (let [md (subs comment 2)
          new-partial (str (or md-partial "")
                           md
                           "\n")]
      (assoc state :md-partial new-partial))))

(defn add-segment-md [{:keys [segments md-partial] :as state}]
  (if (str/blank? md-partial)
    state
    (let [new-seg (->segment-md md-partial)]
      (warn "add segment md: " md-partial)
      (assoc state
             :segments (conj segments new-seg)
             :md-partial ""))))

(defn add-segment-code [{:keys [segments code-partial] :as state} kernel {:keys [tag] :as f}]
  (let [code (str (or code-partial "")
                  (n/string f))
        new-seg (->segment-code kernel code)]
    (assoc state
           :segments (conj segments new-seg)
           :code-partial "")))

(defn process-comment [state {:keys [tag] :as f}]
  (let [c (or (n/string f) "xxx")]
    (warn "comment: " c)
    (if (str/starts-with? c ";;")
      (add-partial-md state c)
      (add-partial-comment state c))))

(defn process-form [kernel state {:keys [tag] :as f}]
  (cond
    (n/comment? f)
    (process-comment state f)

    (n/whitespace? f)
    (do
      (warnf "whitespace node str: " (or (n/string f) "xxx"))
      state)

    :else
    (case (n/tag f)
      :list (-> state
                (add-segment-md) ; add accumulated md (if any)
                (add-segment-code kernel f))
      (do (warnf "ignoring tag: %s form: %s" tag (pr-str f))
          state))))

(defn text->segments [kernel code]
  (debug "text: " code) ; newline :map :comment
  (let [top-forms (->> code
                       (p/parse-string-all)
                       :children)]
    (-> (reduce (partial process-form kernel)
                {:segments []
                 :code-partial ""
                 :md-partial ""}
                top-forms)
        :segments
        (into []))))

(comment
  (text->segments :clj (slurp "goldly/notebooks/bananas.clj")))

(def nb-empty
  {:segments []})

(defn text->notebook [filename kernel code]
  (-> nb-empty
      (assoc :meta {:id (guuid)
                    :title (name filename)
                    :tags #{kernel}})
      (assoc :segments (text->segments kernel code))))

(defn filename->kernel-type [filename]
  (let [m (re-matches #"(.*)\.clj([s]*)" filename)
        [_ name cljs?] m]
    ;(errorf "regex name: %s cljs?: [%s]" name cljs?)
    (if (str/blank? cljs?)
      :clj
      :cljs)))

(defn notebook-load [filename]
  (let [dir  (get-in-config [:goldly :notebook-dir])
        s (load-file! dir filename)
        kernel (filename->kernel-type filename)
        nb (text->notebook filename kernel (:code s))]
    (warn "nb: " nb)
    nb))

(comment
  (notebook-load "bananas.clj"))

(defmethod load-component :notebook [{:keys [id type]}]
  (let [filename (name id)
        data (notebook-load filename)]
    (if data
      {:id id :data data}
      {:id id :error "not found"})))