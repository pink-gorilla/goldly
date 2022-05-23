(ns demo.notebook.goldly-videos)

;; video

(defn youtube [id]
  ['ui.video/video {:box :lg
                    :controls true
                    :url (format "https://www.youtube.com/watch?v=%s" id)}])

(defn show-video [[name id]]
  [:div
   [:h1.text-xl.text-blue-900 name]
   (youtube id)])

(defn video-list [name list]
  ^:R
  [:div
   [:h1.text-3xl.text-blue-900 name]
   (into [:div]
         (map show-video list))])

(def videos
  {:sniffer-repl "HxejHqw4jfI"
   :notebook "8TwXaVTZ1G8"
   :clojisr "BbjYkDmp3fg"})

^:R
[:div.bg-blue-500 ; test how background is on different color
 [:h1 "How to use goldly"]
 (video-list "unsorted videos" videos)]