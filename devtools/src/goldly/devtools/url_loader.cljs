(ns goldly.devtools.url-loader
  (:require
   [reagent.core :as r]
   [http]
   [goldly.service.core :as service]
   [ui.pinkie :refer [error-boundary]]))

;(defn error-boundary [d]
;  [:div d])

(def show-loader-debug-ui false)

; (get-edn "/r/repl/bongo.edn" state [:data])

;http://localhost:8000/api/viewer/file/demo.playground.cljplot/1.txt

(defn load-url [fmt url a args-fetch timeout]
  (let [comparator? (or url args-fetch)
        comparator [url args-fetch]]
    (if comparator?
      (when (not (= comparator (:comparator @a)))
        ;(info (str "loading:  " comparator))
        (swap! a assoc :comparator comparator)
        (case fmt
          :txt (http/get-str url a [:data])
          :edn (http/get-edn url a [:data])
          :clj (if args-fetch
                 (service/run-a-map {:a a
                                     :path [:data]
                                     :fun url
                                     :args args-fetch
                                     :timeout timeout})
                 (service/run-a-map {:a a
                                     :path [:data]
                                     :fun url
                                     :timeout timeout})))

        nil)
      (swap! a assoc :data nil))))

; run-a is not yet perfect. It is difficult to pass args as aparameter
; (run-a state [:version] 'goldly.run.services/goldly-version "goldly")

(defn debug-loader [url data args-render]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "loader debug ui"]
   [:p "url: " url]
   [:p "args-render: " (pr-str args-render)]
   [:p "data: " data]])

(defn url-loader [{:keys [url fmt args-fetch args-render timeout]}
                  fun]
  (let [a (r/atom {:data nil
                   :url nil
                   :arg-fetch nil})]
    (fn [{:keys [url fmt args-fetch args-render timeout]
          :or {fmt :txt
               args-fetch nil
               args-render []
               timeout 60000}}
         fun]
      (load-url fmt url a args-fetch timeout)
      (if-let [d (:data @a)]
        [:div
         [error-boundary
          (if (empty? args-render)
            (fun d)
            (apply fun d args-render))]
         (when show-loader-debug-ui
           [debug-loader url d args-render])]
        (if (= :chsk/timeout @a)
          [:div "timeout: " url]
          [:div "loading: " url])))))
