(ns goldly.nrepl.middleware
  (:require
   ;[clojure.tools.logging :refer (info)]
   [nrepl.transport :as transport]
   [nrepl.middleware.print]
   [nrepl.middleware :as middleware]
   [pinkgorilla.middleware.formatter :as formatter]
   [pinkgorilla.ui.gorilla-renderable :refer [#_render render-renderable-meta]])
  (:import nrepl.transport.Transport))


;; TODO: This might no longer be true as of nrepl 0.6

;; Stolen from:
;; https://github.com/clojure/tools.nrepl/blob/master/src/main/clojure/clojure/tools/nrepl/middleware/pr_values.clj
;; and as a result the structure of this follows that code rather closely

;; This middleware function calls the gorilla-repl render protocol on the value that results from the evaluation, and
;; then converts the result to edn.


(defn render-value [value]
  (let [r (str "XX:" value)]; (render-renderable-meta value)]
    r))

(def c (atom 0))





(defn max-code [msg code]
  (if code (assoc msg :code code) msg))

(defn msg-safe [msg]
  (let [code (:code msg)
        c (if code (count code) 0)
        long? (> c 40)
        code (if long? (subs code 0 40) code)]
    (-> msg
        (max-code code)
        (dissoc :session
                :transport
                :file
                :line
                :column
                :stdout
                :stderr
                :pprint
                :nrepl.middleware.print/keys
                :nrepl.middleware.print/print-fn
                :nrepl.middleware.print/print
                :nrepl.middleware.print/options
                :nrepl.middleware.caught/caught-fn))))

(defn cut-namespaces-val [val]
  (if (get-in val [:namespace-definitions])
    "ns-defs"
    val))


(defn cut-namespaces [msg]
  (if (get-in msg [:value :namespace-definitions])
    (dissoc msg :value)
    msg))

(defn resp-safe [resp]
  (-> resp
      cut-namespaces
      (dissoc :nrepl.middleware.print/keys
              :changed-namespaces)))

(def disabled-ops #{"debug-instrumented-defs"
                    "info"
                    "ns-list"})

(defn convert-response [{:keys [op code cause via trace symbol] :as msg} {:keys [id session ns status value out ns-list completions] :as resp}]
   ;; we have to transform the rendered value to EDN here, as otherwise
   ;; it will be pr'ed by the print middleware (which comes with the
   ;; eval middleware), meaning that it won't be mapped to EDN when the
   ;; whole message is mapped to EDN later. This has the unfortunate side
   ;; effect that the string will end up double-escaped.
   ;; (assoc resp :value (json/generate-string (render/render v)))

  (when (not (contains? disabled-ops op)); (and (nil? completions) (nil? ns-list))
    (spit "nrepl.txt"
          (str "\r\n\r\n" (pr-str (msg-safe msg)))
          :append true)
    (spit "nrepl.txt"
          (str "\r\n" (pr-str (resp-safe resp)))
          :append true))

  (when (and op (= op "eval"))
    (spit "nrepl-eval.txt"
          (str "\r\n" (pr-str {:op op
                               :value (cut-namespaces-val value)
                               :code code
                               :out out}))
          :append true))


  (let [resp-mod
        (case op
          "pinkieeval" (println "evalpinkie" resp)
          "eval" (if-let [[_ v] (and true #_(:as-html msg) (find resp :value))]
                   (assoc resp :pinkie (formatter/serialize (render-value v))))
          nil)]

    (if resp-mod
      (do (spit "nrepl-eval.txt"
                (str "\r\n" (pr-str {:pinkie (:pinkie resp-mod)}))
                :append true)
          resp-mod)
      resp))

  #_(try
      (swap! c inc)
      (when (> @c 5)
        (if (and (not (nil? resp)) (map? resp))
          (println "convert-response ..")
           ;(dissoc resp :ns-list)
              ; (select-keys resp [:id :session])
               ;
          (println "no-map!")))
      (catch clojure.lang.ExceptionInfo e
        (println "convert-response Exception: ")) ; (pr-str e)))
      (catch Exception e
        (println "convert-response Exception: "))); (pr-str e))))
  ;(if-let [[_ v] (and true #_(:as-html msg) (find resp :value))]
  ;  (assoc resp :pinkie (formatter/serialize (render-value v)))
  ;  resp)
 ; resp
  )

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


(defn send-to-pinkie! [{:keys [code] :as req} {:keys [value] :as resp}]
  (when (and code true); (contains? resp :value))
    (println "evalpinkie:" (read-string code) value))
  resp)

(defn- wrap-pinkie-sender
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


(defn wrap-pinkie [handler]
  (fn [{:keys [id op transport] :as request}]
    (if (= op "evalpinkie")
      ;(rebl/ui)
      (handler (assoc request :transport (wrap-pinkie-sender request))))))

(middleware/set-descriptor! #'wrap-pinkie
                            {:requires #{#'nrepl.middleware.print/wrap-print}
                             :expects  #{"eval"}
                             :handles {"evalpinkie" "eval with pinkie conversion"}})