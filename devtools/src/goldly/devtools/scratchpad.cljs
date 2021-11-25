(def empty-scratchpad-hiccup
  [:div ;.bg-blue-500.h-24.pt-3
   [:blockquote.text-xl.italic.ml-10.text-red-600 "Scratchpad is the new REPL!"]
   [:h1.text-xl "your scratchpad is empty!"]
   [:p.mb-3 "you can send hiccup to the browser"]
   [:p.mb-3 "to modify a dom node you can use id scratchpadtest"]
   [user/highlightjs "(goldly.scratchpad/show! [:p \"hello \"])"]])

(def demo-hiccup
  [:p.bg-red-300 "demo123"])

(defonce scratchpad-hiccup
  (r/atom empty-scratchpad-hiccup))

(defonce scratchpad-hiccup-raw
  (r/atom empty-scratchpad-hiccup))

(defn clear-scratchpad [& args]
  (reset! scratchpad-hiccup empty-scratchpad-hiccup)
  (reset! scratchpad-hiccup-raw empty-scratchpad-hiccup))

(defn show-hiccup [h & args]
  (let [h-fn (->hiccup h)]
    (reset! scratchpad-hiccup h-fn)
    (reset! scratchpad-hiccup-raw h)))

; eval

(defonce scratchpad-show
  (r/atom false))

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

(defn scratchpad []
  [:div.w-full.h-full.m-0.p-5  ;[:div.bg-green-300.w-screen.h-screen.overflow-scroll
   ; header
   [:div.pt-5
    [:span.text-xl.text-blue-500.text-bold.mr-4 "scratchpad"]
    [:button.bg-gray-400.m-1 {:on-click clear-scratchpad} "clear"]
    [:button.bg-gray-400.m-1 {:on-click #(show-hiccup demo-hiccup)} "demo"]
    [:button.bg-gray-400.m-1 {:on-click #(swap! scratchpad-show not)}
     (if @scratchpad-show
       "hide repl"
       "show repl")]]
   ; eval
   (when @scratchpad-show
     [:div
      [:p.text-xl.text-blue-500.mt-3.mb-3 "eval"]
       ;[highlightjs {:code @scratchpad-code}]
      [codemirror 27 scratchpad-code]
      [:button.bg-gray-400.m-1 {:on-click eval-cljs} "eval cljs"]
      (when @cljs-er
        [:div (pr-str @cljs-er)])])
   ; hiccup
   [:p.text-xl.text-blue-500.mt-3.mb-3 "output"]
   [:div#scratchpadtest]
   [:div.w-full
    @scratchpad-hiccup]
   ; separator
   [:hr.mt-5]
   ; hiccup (source)
   [:p.text-xl.text-blue-500.mt-3.mb-3 "hiccup"]
   [:div.bg-gray-300.overflow-scroll.w-full (pr-str @scratchpad-hiccup-raw)]])

(add-page-template scratchpad :scratchpad)

; {:op     :show :clear
;  :hiccup [:p "hi"]
;  :ns     demo.playground.cljplot

(defn process-scratchpad-op [{:keys [op hiccup] :as msg}]
  (case op
    :clear (clear-scratchpad)
    :show  (show-hiccup hiccup)
    (println "unknown viewer op:" op)))

(rf/reg-event-fx
 :scratchpad/msg
 (fn [{:keys [db]} [_ msg]]
   (println "scratchpad msg received: " msg)
   (process-scratchpad-op msg)
   nil))

(rf/reg-event-fx
 :scratchpad/get
 (fn [{:keys [db]} [_ msg]]
   (let [h (or @scratchpad-hiccup-raw [:div "empty scratchpad"])]
     (info "scratchpad get:" msg)
     (rf/dispatch [:goldly/send :scratchpad/state h]))
   nil))
