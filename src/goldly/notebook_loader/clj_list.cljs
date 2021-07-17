(ns goldly.notebook-loader.clj-list
  (:require
   [taoensso.timbre :as timbre :refer-macros [debugf infof error]]
   [reagent.ratom :refer-macros [run!]]
   [re-frame.core :as rf]
   [ui.notebook.loader.list] ; side effects
   ))

(defn nb [name]
  {:name name
   :type :clj})

(defn index->nb-list [names]
  {:name "clj"
   :notebooks (map nb names)})

;{:name "clj"
; :notebooks ({:name [:notebooks ["bananas.clj" "apple.clj"]]
;              :type :clj})}

(defn on-index-change [{:keys [notebooks]
                        :or {notebooks []}}]
  (infof "clj nbs changed count: %s" (count notebooks))
  (when notebooks
    (let [nbs-clj (index->nb-list notebooks)]
      (rf/dispatch [:notebook-list/set nbs-clj])
      (debugf "clj list: %s" nbs-clj))))

(defn start-watch-notebooks []
  (let [names (rf/subscribe [:index/show :notebook])]
    (on-index-change @names)
    (run!
     (on-index-change @names))))
