(ns goldly.sci.loader.cljs-source-http
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [cljs.core.async :refer [<! >! chan close!] :refer-macros [go]]
   [cljs-http.client :as http]
   [goldly.sci.loader.cljs-source :refer [ns->filename on-cljs-received]]
   [goldly.sci.loader.static :refer [dynamic-base]]))

(defn filename-to-url-goldly [filename]
  (str "/code/" filename))

(defn filename-to-url-github [filename]
  (let [base (dynamic-base)]
    (str base "/code/" filename)))

(def filename-to-url
  (atom filename-to-url-goldly))

(defn set-github-load-mode []
  (reset! filename-to-url filename-to-url-github))

(defn load-module-sci [{:keys [ctx libname ns opts property-path] :as d}]
  ; libname: bongo.trott ; the ns that gets compiled
  ; ns:  demo.notebook.applied-science-jsinterop ; the namespace that is using it
  ; opts: {:as bongo, :refer [saying]}
  ; ctx is the sci-context
  (info "load-sci-src" "libname:" libname "ns: " ns "opts:" opts)
  (let [filename (-> libname str ns->filename (str ".cljs"))
        url (@filename-to-url filename)]
    (info "loading filename: " filename)
    (js/Promise.
     (fn [resolve reject]
       (go (let [opts (or opts {:with-credentials? false})
                 response (<! (http/get url opts))
                 status (:status response)
                 code (:body response)]
             (info "load-module-sci-cljs url: " url "status: " status)
             (if (= status 200)
               (on-cljs-received ctx libname ns opts resolve reject [:http-load {:result {:code code}}])
               (reject (str "failed to load: " libname " status: " status " url: " url)))))))))
