(ns ui.goldly.core
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [goldly.sci.bindings :refer [add-cljs-namespace add-cljs-bindings generate-bindings]]))

; cljs ui


(add-cljs-namespace [ui.goldly.code.core]) ; for code viewer bundle inclusion
(add-cljs-namespace [ui.codemirror.goldly.core]) ; codemirror


; cljs functions
(add-cljs-namespace [ui.goldly.fun]) ; test if bindings generation works

(add-cljs-bindings {; goldly
                    'set-system-state ui.goldly.fun/set-system-state
                    'nav ui.goldly.fun/nav
                    'clipboard-set ui.goldly.fun/clipboard-set
                    'clipboard-pop ui.goldly.fun/clipboard-pop
                    'timeout ui.goldly.fun/timeout
                    'alert ui.goldly.fun/alert
                    'evt-val ui.goldly.fun/evt-val
                    'modal ui.goldly.fun/modal
                    ; logging
                    'println println
                    'info ui.goldly.fun/info
                    'warn ui.goldly.fun/warn
                    'error ui.goldly.fun/error
                    ; math
                    'sin ui.goldly.fun/sin ; test bindings
                ;
                    })