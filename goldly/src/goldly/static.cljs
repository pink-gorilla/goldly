(ns goldly.static
  (:require
   [taoensso.timbre :refer-macros [debug info infof error]]
   [ajax.core :refer [GET]]
   [cljs.core.async :refer [>! <! chan close! put! timeout] :refer-macros [go]]
   [cljs.reader :refer [read-string]]
   [webly.build.prefs :refer [pref]]))

(defn static? []
  (let [pref (pref)
        profile (:profile pref)
        static? (= "static" profile)]
    (info "pref:" pref)
    (info "static?: " static?)
    static?))

(defn error-handler [& args]
  (error "error getting data ..."))

(defn make-url [ext]
  (let [pref (pref)
        asset-path (:asset-path pref)]
    (str asset-path "/" ext)))

(defn get-url [uri]
  (let [ch (chan)
        handler (fn [data]
                  (infof "static/get  %s" uri)
                  (let [d (read-string data)]
                    (info "data: " d)
                    (put! ch {:result d})))]
    (GET uri {:handler handler
              :error-handler error-handler})
    ch))

(defn cljs-explore []
  (let [url (make-url "sci-cljs-autoload.edn")]
    (get-url url)))

(defn extensions []
  (let [url (make-url "extensions.edn")]
    (get-url url)))

(defn get-ext-static [ext-name]
  (go (let [ch (chan)
            data (<! (extensions))
            _ (info "data: " data)
            e (filter #(= ext-name (:name %)) data)]
        (info "ext: " e)
        (>! ch e))))

(defn get-code [filename]
  (let [uri (make-url filename)
        ch (chan)
        handler (fn [data]
                  (infof "static/get-code received code for: %s" filename)
                  (let [d {:code data
                           :filename filename}]
                    ;(info "code: " d)
                    (put! ch {:result d})))]
    (info "getcode url: " uri)
    (GET uri {:handler handler
              :error-handler error-handler})
    ch))

