(ns goldly.notebook-loader.clj-list
  (:require
   [taoensso.timbre :as timbre :refer-macros [debugf infof error]]
   [reagent.ratom :refer-macros [run!]]
   [re-frame.core :as rf]
   [ui.notebook.loader.list] ; side effects
   ))
;{:name "collection-demo1"
; :notebooks [{:name "bananas.clj"
;              :type :clj} 
;             {:name "apple.clj"
;              :type :clj}]}

(defn on-index-change [nb-collections]
  (infof "clj nbs changed count: %s" (count nb-collections))
  (error "nb collections: " nb-collections)
  (when nb-collections
    (doall (map #(rf/dispatch [:notebook-list/set %]) nb-collections))))

(defn start-watch-notebooks []
  (let [names (rf/subscribe [:index/show :notebook])]
    (on-index-change @names)
    (run!
     (on-index-change @names))))
