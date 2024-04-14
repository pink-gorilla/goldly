(ns reval.goldly.page.eval-remote
  (:require
   [re-frame.core :as rf]
   [modular.ws.core :as ws]
   [goldly.sci :refer [compile-sci-async]]))

(defn send-result! [r]
  (try
    (ws/send! [:cljs/result r])
    (catch js/Exception e
      (ws/send! [:cljs/result (merge (dissoc r :error :result)
                                     {:error :exception})]))))

(defn sci-error [e]
  (let [data (ex-data e)]
    (if-let [message (or (:message data) (.-message e))]
      message
      "unknown sci error")))

(defn remote-eval [{:keys [code _ns]}]
  (let [eval-result-promise (compile-sci-async code)]
    (-> eval-result-promise
        (.then  (fn [r]
                  (send-result! {:code code :result r})))
        (.catch (fn [e]
                  (println "eval error: " e)
                  (send-result! {:code code :error (sci-error e)}))))))

(rf/reg-event-fx
 :cljs/eval
 (fn [{:keys [_db]} [_ code]]
   ;(println "repl msg received: " msg)
   (remote-eval code)
   nil))

