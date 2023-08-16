(ns demo.static.demo
  (:require
   [clojure.edn :as edn]
   [promesa.core :as p]
   [re-frame.core :as rf]
   [ajax.core :refer [GET]]
   [modular.log]
   [ui.bidi]
   [ui.css]
   [demo.page.main] ; side-effects
   [goldly.devtools.page.help]
   [goldly.devtools.page.build]
   [goldly.devtools.page.page-list]
   [goldly.devtools.page.runtime]
   [goldly.devtools.page.theme]
   [reval.goldly.page.repl]
   [reval.goldly.page.notebook-viewer]))

(defn edn-get [url]
  (p/let [resp (GET url)
          data (edn/read-string resp)]
    (println "edn RCVD: " data)
    data))

(def routes
  ["/"
   {"index.htm" :user/main
    ;devtools
    "devtools/help" :devtools
    "devtools/repl" :repl
    "devtools/pages" :pages
    "devtools/viewer" :viewer
    "devtools/build" :build
    "devtools/runtime" :runtime
    "devtools/theme" :theme}])

(rf/reg-event-db
 :demo/config
 (fn [db [_ data]]
   (println "demo-config: " data)
   (assoc db :config data)))

(defn init []

;  (modular.log/timbre-config!
;      {:min-level [[#{"webly.*"} :info]
;                   [#{"*"} :info]]})

  (rf/dispatch [:demo/config {:prefix "/r/" ; used by css-loader
                              :x 42}])

  ; CSS
  ; add static generated themes.
  (-> (edn-get "/css-theme-static.edn")
      (p/then (fn [theme]
                (println "static theme: " theme)
                (rf/dispatch [:css/add-components (:available theme) (:current theme)])))
      (p/catch (fn [err]
                 (println "get static theme error: " err))))

  ; make sure css loader gets started:
  (rf/dispatch [:webly/status-css :loaded])
  (rf/dispatch [:webly/set-status :configured? true])

  (println "initializing bidi router...")
  (ui.bidi/start-router! routes "")
  ;(ui.bidi/goto! :user/main)
  (println "init done!"))

(defn router-page []
  (let [configured? (rf/subscribe [:webly/status-of :configured?])
        css-links (rf/subscribe [:css/app-theme-links])]
    (fn []
      [:div
      ;[:p "configured: " (str @configured?)]
      ;[:p "css-links: " (pr-str @css-links)]
       [ui.css/load-css]
       [ui.bidi/page-viewer]])))

