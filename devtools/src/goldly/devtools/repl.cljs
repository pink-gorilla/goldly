; eval

(def demo-code "(* 6 (+ 7 7))")

(defonce scratchpad-code
  (r/atom "(+ 7 7)"))

(defonce cljs-er
  (r/atom nil))

(defn eval-cljs []
  (let [code @scratchpad-code
        _ (println "eval cljs: " code)
        er (compile-sci code)]
    (println "cljs eval result:" er)
    (reset! cljs-er er)))

(defn repl-header []
  [:div.pt-5
   [:span.text-xl.text-blue-500.text-bold.mr-4 "repl"]
   [:button.bg-gray-400.m-1 {:on-click #(reset! scratchpad-code demo-code)} "demo"]
   [:span "ns: " "user"]
   [:button.bg-gray-400.m-1 {:on-click eval-cljs} "eval cljs"]])

(defn repl-output []
  [:div
   [:p.text-xl.text-blue-500.mt-3.mb-3 "output"]
   [:div#scratchpadtest]
   (when @cljs-er
     [:div (pr-str @cljs-er)])])

(defn style-codemirror-fullscreen []
  ; height: auto; "400px" "100%"  height: auto;
  [:style ".my-codemirror > .CodeMirror { 
              font-family: monospace;
              height: 100% ;
              min-height: 100%;
            }"])

(defn repl []
  [spaces/viewport
   [spaces/top-resizeable {:size 50}
    ;"top"
    [repl-header]] ; 
   [spaces/fill {:class "bg-white-500"}
    [:div
     [spaces/left-resizeable {:size "40%"}
      [:div.w-full.h-full.bg-white-200
       [style-codemirror-fullscreen]
       [codemirror 27 scratchpad-code]]]

     [spaces/fill {}
      [repl-output]]]]])

(add-page repl :repl)

; {:op     :show :clear
;  :hiccup [:p "hi"]
;  :ns     demo.playground.cljplot

(defn remote-eval [code]
  (println "remote eval: " code)
  (let [eval-result (compile-sci code)]
     ;(rf/dispatch [:goldly/send :scratchpad/evalresult {:code code :result eval-result}])
     ;(run-cb {:fun :scratchpad/evalresult :args {:code code :result eval-result}})
    (send! [:scratchpad/evalresult {:code code :result eval-result}] (fn [& _]) 2000)))

(defn process-repl-op [{:keys [op hiccup code] :as msg}]
  (case op
    ;:clear (clear-scratchpad)
    ;:show  (show-hiccup hiccup)
    :eval (remote-eval code)
    (println "unknown op:" op)))

(rf/reg-event-fx
 :repl/msg
 (fn [{:keys [db]} [_ msg]]
   (println "repl msg received: " msg)
   (process-repl-op msg)
   nil))

