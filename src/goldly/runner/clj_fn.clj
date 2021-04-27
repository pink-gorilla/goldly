(ns goldly.runner.clj-fn
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [debug info infof error errorf]]
   [goldly.puppet.db :refer [get-system]]))

(defn run-system-fn-clj [id fun-kw args]
  (infof "run-system-fn-clj system %s fun: %s" id fun-kw)
  (let [system (get-system (keyword id))]
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