(ns goldly.cljs.nbcollection)

(def nbdata (r/atom nil))

(get-edn "/r/notebook/demo-notebook.edn" nbdata [])

(defn add-demo-notebook-collection [nb]
  (let [nb-list-embed {:name "cljs"
                       :notebooks [{:name "cljs"
                                    :type :embedded
                                    :data nb}
                                   {:name "t"
                                    :type :template}]}]
    (rf/dispatch [:notebook-list/set nb-list-embed]
                 (rf/dispatch [:doc/load nb]))))

@nbdata