(ns demo.notebook.err-clj
  (:require [bongistan]))

(blubb 1234)

; {:err {:class "java.io.FileNotFoundException", 
;         :message "Could not locate bongistan__init.class, bongistan.clj or bongistan.cljc on classpath.", 
;         :stacktrace [] }