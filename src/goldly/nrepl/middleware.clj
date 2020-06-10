(ns goldly.nrepl.middleware
  (:require
   ;[clojure.tools.logging :refer (info)]
   [nrepl.transport :as transport]
   [nrepl.middleware :as middleware]
   [nrepl.middleware.print]
   [pinkgorilla.middleware.formatter :as formatter]
   [goldly.nrepl.ignore :refer [ignore?]]
   [goldly.nrepl.logger :as logger]
   [goldly.nrepl.snippets :as snippets]
   [systems.snippets :refer [publish-eval!]])
  (:import nrepl.transport.Transport))

; nrepl docs:
; https://nrepl.org/nrepl/ops.html#_add_middleware

; a clean nrepl middleware is found in:
; https://github.com/RickMoynihan/nrebl.middleware/blob/master/src/nrebl/middleware.clj

;; Stolen from:
;; https://github.com/clojure/tools.nrepl/blob/master/src/main/clojure/clojure/tools/nrepl/middleware/pr_values.clj
;; and as a result the structure of this follows that code rather closely

;; This middleware function calls the gorilla-repl render protocol on the value that 
;; results from the evaluation, and then converts the result to edn.

(defn convert-response [msg resp]
   ;; we have to transform the rendered value to EDN here, as otherwise
   ;; it will be pr'ed by the print middleware (which comes with the
   ;; eval middleware), meaning that it won't be mapped to EDN when the
   ;; whole message is mapped to EDN later. This has the unfortunate side
   ;; effect that the string will end up double-escaped.
   ;; (assoc resp :value (json/generate-string (render/render v)))
  (if (ignore? msg resp)
    resp
    (do
      (logger/on-nrepl-eval msg resp)
      (if-let [eval-result (snippets/on-nrepl-eval msg resp)]
        (do (publish-eval! eval-result)
            (assoc resp :pinkie (formatter/serialize (:pinkie eval-result)))) ; this is used by the notebook
        resp))))

(defn render-values
  [handler]
  (fn [{:keys [^Transport transport] :as msg}]
    (handler
     (assoc msg :transport
            (reify Transport
              (recv [this]
                (println "rcvd!")
                (.recv transport))
              (recv [this timeout] (.recv transport timeout))
              (send [this resp]
                (.send transport (convert-response msg resp))
                this))))))


;; TODO: No idea whether this still applies to nrepl 0.6
;; nrepl.middleware.print/wrap-print is the new nrepl.middleware.pr-values - see new CHANGELOG.md
;; Unfortunately nREPL's interruptible-eval middleware has a fixed dependency on the pr-values middleware. So here,
;; what we do is fudge the :requires and :expects values to ensure that our rendering middleware gets inserted into
;; the linearized middlware stack between the eval middleware and the pr-values middleware. A bit of a hack!

(middleware/set-descriptor! #'render-values
                            {:requires #{#'nrepl.middleware.print/wrap-print}
                             :expects  #{"eval"}
                             :handles {"pinkieeval" "eval with pinkie conversion"}})


#_(defn send-to-pinkie! [{:keys [code] :as req} {:keys [value] :as resp}]
    (when (and code true); (contains? resp :value))
      (println "evalpinkie:" (read-string code) value))
    resp)

#_(defn- wrap-pinkie-sender
    "Wraps a `Transport` with code which prints the value of messages sent to
  it using the provided function."
    [{:keys [id op ^Transport transport] :as request}]
    (reify transport/Transport
      (recv [this]
        (.recv transport))
      (recv [this timeout]
        (.recv transport timeout))
      (send [this resp]
        (.send transport
               (send-to-pinkie! request resp))
        this)))

#_(defn wrap-pinkie [handler]
    (fn [{:keys [id op transport] :as request}]
      (if (= op "evalpinkie")
      ;(rebl/ui)
        (handler (assoc request :transport (wrap-pinkie-sender request))))))

#_(middleware/set-descriptor! #'wrap-pinkie
                              {:requires #{#'nrepl.middleware.print/wrap-print}
                               :expects  #{"eval"}
                               :handles {"evalpinkie" "eval with pinkie conversion"}})