(ns goldly.store.loader
  (:require
   [taoensso.timbre :refer-macros [trace debug debugf info infof warn warnf error errorf]]
   [cljs.core.async :refer [>! <! chan close! put!] :refer-macros [go]]
   [goldly.service.core :refer [run]]
   [goldly.sci.kernel-cljs :refer [compile-code]]))

(defn compile-cljs [{:keys [filename code]}]
  (info "compiling" filename ": " code)
  (compile-code code))

(defn load-cljs []
  (warn "load-cljs")
  (go
    (let [sci-result  (<! (run {:fun :status/sci}))
          extensions-result  (<! (run {:fun :status/extensions}))
          files-result (<! (run {:fun :cljs/explore}))
          sci (:result sci-result)
          extensions (:result extensions-result)
          files (:result files-result)]
      (info "sci: " (pr-str sci))
      (info "extensions: " (pr-str extensions))
      (info "cljs files: " (pr-str files))
      (doall
       (for [f files]
         (go
           (let [{:keys [error result] :as r} (<! (run {:fun :cljs/load :args [f]}))]
             (when error
               (warn "error loading cljs: " r))
             (when result
               (compile-cljs result))))))

;      
      )))

