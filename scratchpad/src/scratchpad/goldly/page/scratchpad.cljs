(def empty-scratchpad-hiccup
  [:div ;.bg-blue-500.h-24.pt-3
   [:blockquote.text-xl.italic.ml-10.text-red-600 "Goldly Scratchpad"]
   [:h1.text-xl "scratchpad is empty!"]])

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
  (let [h-fn (render-vizspec h)]
    (reset! scratchpad-hiccup h-fn)
    (reset! scratchpad-hiccup-raw h)))

; eval

(defonce scratchpad-show
  (r/atom false))

(defn scratchpad []
  [:div.w-full.h-full.m-0.p-5  ;[:div.bg-green-300.w-screen.h-screen.overflow-scroll
   ; header
   [:div.pt-5
    [:span.text-xl.text-blue-500.text-bold.mr-4 "scratchpad"]
    [:button.bg-gray-400.m-1 {:on-click clear-scratchpad} "clear"]
    [:button.bg-gray-400.m-1 {:on-click #(show-hiccup demo-hiccup)} "demo"]]
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

(add-page scratchpad :scratchpad)

(defn process-scratchpad-op [{:keys [op hiccup code] :as msg}]
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
