(ns goldly.core
  (:require
   [clojure.string]
   [clojure.core.async :as async  :refer (<! <!! >! >!! put! chan go go-loop)]
   [taoensso.timbre :as log :refer (tracef debug debugf info infof warnf error errorf)]
   [cemerick.pomegranate :as pg]
   [goldly.ws :refer [send-all! chsk-send! -event-msg-handler connected-uids]]))

(defn unique-id
  "Get a unique id."
  []
  (str (java.util.UUID/randomUUID)))

(defn add-dependencies
  "Use Pomegranate to add dependencies 
   with Maven Central and Clojars as default repositories.
   Same Syntax as clojupyter
   stolen from: https://github.com/clojupyter/clojupyter/blob/40c6d47ec9c9e4634c8e28fca3209b5c3ac8430c/src/clojupyter/misc/helper.clj

   "
  [dependencies & {:keys [repositories]
                   :or {repositories {"central" "https://repo1.maven.org/maven2/"
                                      "clojars" "https://clojars.org/repo"}}}]
  (let [first-item (first dependencies)]
    (if (vector? first-item)
      ; [ [dep1] [dep2]]
      (pg/add-dependencies :coordinates `~dependencies
                           :repositories repositories)
      ; [dep1]
      (pg/add-dependencies :coordinates `[~dependencies]
                           :repositories repositories))))


;; system


(def systems (atom {}))

(defn systems-response []
  (let [;_ (println "systems-response: " @systems)
        summary (into []
                      (map (fn [[k v]]
                             {:id (name k)
                              :name (or (:name v) "")}) @systems))
        ;ids (keys @systems)
        ;ids (into [] (map name ids))
        ]
    [:goldly/systems #_ids summary]))

(defn system->cljs
  "converts a system from clj to cljs"
  [system]
  (let [clj (or (get-in system [:clj :fns]) {})
        system-cljs (-> system
                        (dissoc :clj)
                        (assoc :fns-clj (into [] (keys clj))))]

    (println "system-cljs: " system-cljs)
    system-cljs))

(defn system-response
  "gets system to be sent to clj"
  [id]
  (let [id (keyword id)
        system (id @systems)]
    (when system
      (system->cljs system))))

(defn send-event [system-id event-name & args]
  (let [message  {:system system-id :type event-name :args args}]
    (send-all! [:goldly/event message])))

(defn into-mapper
  "applies function f on all values of a map.
   returns a map with the same keys"
  [f m]
  (into (empty m) (for [[k v] m] [k (f v)])))

(defmacro system [{:keys [name state html fns] :as system-cljs} system-clj]
  (let [fns (zipmap (keys fns)
                    (map #(pr-str %) (vals fns)))]
    {:id (unique-id)
     :name name
     :cljs {:state state
            :html (pr-str html)
            :fns (into-mapper pr-str fns)}
     :clj system-clj}))

(comment
  (macroexpand (system {:html [:h1] :fns {:a 6 :b 8 :c "g"} :state 9} 2)))

(defmethod -event-msg-handler :goldly/systems
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid (:uid session)]
    (tracef "systems event: %s" event)
    (when ?reply-fn
      (?reply-fn (systems-response)))))

(defmethod -event-msg-handler :goldly/system
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid (:uid session)
        [event-name system-id] event]
    (infof "rcvd  %s %s" event-name system-id)
    (let [response (system-response system-id)]
      (if response
        (if ?reply-fn
          (?reply-fn response)
          (chsk-send! uid [:goldly/system response]))
        (info ":goldly/system request for unknown system: " system-id)))))

(defn run-system-fn-clj [id fun-kw args]
  (infof "run-system-fn-clj system %s fun: %s" id fun-kw)
  (let [system ((keyword id) @systems)]
    (if system
      (let [fun-vec (get-in system [:clj :fns fun-kw])]
        (if fun-vec
          (let [[fun where] fun-vec]
            (infof "system %s executing fun: %s args: %s" id fun-kw args)
            {:result (if args
                       (apply fun args)
                       (fun))
             :where where})
          (do (errorf "system %s : fn not found: %s" id fun-kw)
              (error "system: " system)
              {:error (str "function not found: " fun-kw)})))
      (do (infof "system %s : system not found. fn: %s" id fun-kw)

          {:error (str "system " id "not found, cannot execute function: " fun-kw)}))))

(defn create-clj-run-response [run-id id fun-kw args]
  (let [result (try (run-system-fn-clj id fun-kw args)
                    (catch clojure.lang.ExceptionInfo e
                      {:error (str "Exception: " (pr-str e))})
                    (catch Exception e
                      {:error (str "Exception: " (pr-str e))}))]
    (merge {:run-id run-id
            :system-id id
            :fun fun-kw} result)))

(defmethod -event-msg-handler :goldly/dispatch
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid (:uid session)
        [event-name [run-id system-id fun & args]] event]
    (infof "rcvd %s runner: %s system: %s fun: %s args: %s" event-name run-id system-id fun args)
    (let [response (create-clj-run-response run-id system-id fun args)
          _ (debug "response: " response)]
      (if ?reply-fn
        (?reply-fn response)
        (chsk-send! uid [:goldly/dispatch response])))))

(defn dispatch [system-id event-name & args]
  (println "dispatching " system-id event-name)
  (send-event system-id event-name args))

(add-watch connected-uids :connected-uids
           (fn [_ _ old new]
             (when (not= old new)
               (infof "Connected uids change: %s" new)
               (let [uids (:any new)]
                 (info "uids: " uids)
                 (doseq [uid uids]
                   (info "sending systems info to: " uid)
                   (chsk-send! uid (systems-response)))))))

(defn system-start!
  [system]
  (println "starting system " (:id system))
  (swap! systems assoc (keyword (:id system)) system)
  (system->cljs system))

(def broadcast-enabled?_ (atom true))

(defn start-heartbeats!
  "setup a loop to broadcast an event to all connected users every second"
  []
  (go-loop [i 0]
    (<! (async/timeout 60000))
    (when @broadcast-enabled?_ (send-all! (systems-response)))
    (recur (inc i))))

;(start-heartbeats!)

