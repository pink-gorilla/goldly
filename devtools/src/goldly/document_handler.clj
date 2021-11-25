(ns goldly.document-handler
  (:require
   [taoensso.timbre  :refer [debug info warn error]]
   [ring.util.response :as res :refer [not-found file-response resource-response response]]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.not-modified :refer [wrap-not-modified]]
   [modular.persist.protocol :as p]
   [modular.webserver.middleware.api :refer [wrap-api-handler]]
   [modular.webserver.handler.registry :refer [add-ring-handler]]
   [reval.document.manager :as dm]))

; url:
; http://localhost:8000/api/rdocument/file/demo.notebook.apple/notebook.edn

(defn ns-file-handler
  "ring handler to serve files in reproduceable ns folder
   it needs to be added to the routing-table
   parameter: ns + filename"
  [req]
  (let [params (:params req)
        {:keys [ns name]} params]
    (info "nb resource file handler: ns:" ns "name:" name)
    (if-let [fmt (p/filename->format name)] ; todo: eventually do not check format here at all ?
      (if-let [file-name (dm/get-filename-ns ns name)]
        (do
          (info "serving ns-file: " file-name "format: " fmt)
          (file-response file-name))
        (do (error "viewer filename cannot be created: " ns name)
            (not-found {:body (str "filename cannot be created: " ns name)})))
      (do (error (str "viewer file resource - format could not be determined for name: [" name "]"))
          (not-found {:error (str "format could not be determined: " name)})))))

(def wrapped-ns-file-handler
  (-> ns-file-handler
      (wrap-content-type) ; options
      (wrap-not-modified)))

(add-ring-handler :rdocument/file wrapped-ns-file-handler)

;; rest

(defn get-ns-list
  "ring handler for rest endpoint 
   returns namespaces that have reproduceable documents."
  [req]
  (let [ns-list (dm/get-ns-list)]
    (debug "notebook list user: " ns-list)
    (response {:data ns-list})))

(defn get-ns-files
  "ring handler for rest endpoint 
   returns document names in reproduceable document namespace.
   parameter: ns"
  [req]
  (let [params (:params req)
        {:keys [ns]} params
        filename-list (dm/get-document-list ns)]
    (debug "resources for notebook " ns ": " filename-list)
    (response {:data filename-list})))

(add-ring-handler :rdocument/ns (wrap-api-handler get-ns-list))
(add-ring-handler :rdocument/files (wrap-api-handler get-ns-files))

(comment

  ; (loadr "demo.studies.asset-allocation-dynamic" "2" :text)

  (ns-file-handler
   {:params {:nbns "demo.studies.asset-allocation-dynamic"
             :name "1.edn"}})

  (ns-file-handler
   {:params {:nbns "demo.studies.asset-allocation-dynamic"
             :name "2.txt"}})

  (get-ns-files "ta.notebook.persist")

;  
  )