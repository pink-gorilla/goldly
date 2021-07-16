(ns goldly.notebook-loader.demo
  (:require
   [re-frame.core :as rf]
   [goldly.data.notebook :as data]))

(def nb-list-embed
  {:name "cljs"
   :notebooks [{:name "cljs"
                :type :embedded
                :data data/notebook}
               {:name "t"
                :type :template}]})

(defn load-demo-notebooks []
  (rf/dispatch [:doc/load data/notebook])
  (rf/dispatch [:notebook-list/set nb-list-embed]))