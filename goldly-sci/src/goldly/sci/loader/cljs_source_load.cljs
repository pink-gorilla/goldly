(ns goldly.sci.loader.cljs-source-load
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [clojure.string]
   [promesa.core :as p]
   [ajax.core :refer [GET]]
   [webly.spa.mode :refer [get-mode get-resource-path]]))

(defn wrap-promise
  ; see in sci-configs ajax.promise
  [AJAX-TYPE url params]
  (info "getting url: " url)
  (p/create
   (fn [resolve reject]
     (AJAX-TYPE url
                (merge params
                       {:handler (fn [response]
                                    (info "sci source received successfully for url: " url)
                                   (resolve response))
                        :error-handler (fn [error]
                                         (error "sci source load error url: " url " error: " error)
                                         (reject error))})))))

(defn GET-p [url] 
  (wrap-promise GET url {:format :text
                         :response-format  (ajax.core/text-response-format) ;; IMPORTANT!: You must provide this.
                         }))

(defn get-code [url]
  ;; Using native JS fetch API
  (-> (js/fetch url)
      (.then (fn [res]
               (if (.-ok res)
                 (.text res)
                 (js/Promise.reject (str "HTTP error " (.-status res))))))))

(defn ns->filename [ns]
  (-> ns
      (clojure.string/replace #"\." "/")
      (clojure.string/replace #"\-" "_")))

(defn filename-dynamic [filename]
  (str "/code/" filename))

(defn filename-static [filename]
  (str (get-resource-path) "code/" filename))

(defn ns->url [ns]
  (let [file (ns->filename ns)
        mode (get-mode)
        url (case mode
              :static (filename-static file)
              :dynamic (filename-dynamic file)
              (filename-dynamic file))
        url (str url ".cljs")]
    (info "loading sci-cljs source file from url: " url)
    url))

(defn load-sci-cljs-code [libname]
  ; libname: bongo.trott ; the ns that gets compiled
  (let [url (-> libname str ns->url)]
    (info "load-sci-cljs-code" "libname:" libname " url: " url)  
    ;(GET-p url)
    (get-code url)
    ))
