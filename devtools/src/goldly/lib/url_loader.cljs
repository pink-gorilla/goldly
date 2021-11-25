(def show-loader-debug-ui false)

; (get-edn "/r/repl/bongo.edn" state [:data])

;http://localhost:8000/api/viewer/file/demo.playground.cljplot/1.txt

(defn load-url [fmt url a arg-fetch args-fetch]
  (let [comparator? (or url arg-fetch args-fetch)
        comparator [url arg-fetch args-fetch]]
    (if comparator?
      (when (not (= comparator (:comparator @a)))
        (info (str "loading:  " comparator))
        (swap! a assoc :comparator comparator)
        (case fmt
          :txt (http/get-str url a [:data])
          :edn (http/get-edn url a [:data])
          :clj (if arg-fetch
                 (run-a a [:data] url arg-fetch)
                 (if args-fetch
                   (apply run-a a [:data] url args-fetch)
                   (run-a a [:data] url))))

        nil)
      (swap! a assoc :data nil))))

; run-a is not yet perfect. It is difficult to pass args as aparameter
; (run-a state [:version] :goldly/version "goldly")

(defn debug-loader [url data args-render]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "loader debug ui"]
   [:p "url: " url]
   [:p "args-render: " (pr-str args-render)]
   [:p "data: " data]])

(defn url-loader [{:keys [url fmt arg-fetch args-fetch args-render]}
                  fun]
  (let [a (r/atom {:data nil
                   :url nil
                   :arg-fetch nil})]
    (fn [{:keys [url fmt arg-fetch args-fetch args-render]
          :or {fmt :txt
               arg-fetch nil
               args-fetch nil
               args-render []}}
         fun]
      (load-url fmt url a arg-fetch args-fetch)
      (if-let [d (:data @a)]
        [:div
         [error-boundary
          (if (empty? args-render)
            (fun d)
            (apply fun d args-render))]
         (when show-loader-debug-ui
           [debug-loader url d args-render])]
        [:div "loading: " url]))))