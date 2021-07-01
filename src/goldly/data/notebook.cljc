(ns goldly.data.notebook)
(def notebook
  {:meta {:id :7c9ab23f-c32f-4879-b74c-de7835ca8ba4
          :title "sci cljs demo"
          :tags #{:demo :sci :cljs}}
   :segments
   [{:id 1
     :type :code
     :data {:kernel :cljs
            :code "; eval cljs\n(+ 7 7)"}
     :state nil}
    {:id 2
     :type :code
     :data {:kernel :cljs
            :code "(notify \"Welcome to cljs repl\")"}}
    {:id 3
     :type :code
     :data {:kernel :cljs
            :code "{:a 1 :b [7 8]}"}}
    {:id 4
     :type :code
     :data {:kernel :cljs
            :code (str "(def a (atom {:a 12}))\n"
                       "(run-a a [:a] :demo/add 1 1)")}}

    {:id 5
     :type :code
     :data {:kernel :cljs
            :code (str "; check if run-a got the data...\n"
                       "@a")}}

    {:id 6
     :type :code
     :data {:kernel :cljs
            :code (str "; inspect app-db ..\n"
                       " (-> (app-db)\n"
                       "     keys)")}}

    {:id 7
     :type :code
     :data {:kernel :cljs
            :code (str "; bidi route navigation ..\n"
                       ";(rf/dispatch [:bidi/goto \"/\"])\n")}}

    {:id 8
     :type :code
     :data {:kernel :cljs
            :code (str "; bidi current route ..\n"
                       "(current-route)")}}

    {:id 9
     :type :code
     :data {:kernel :cljs
            :code (str "; current reframe subscription ..\n"
                       "@(rf/subscribe [:webly/status-show-app])")}}]})