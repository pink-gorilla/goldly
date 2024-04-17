(ns demo.notebook.goog-string
  (:require
   [goog.string :refer [format]]))


(format "hello %s !" "mr X.")

(format "%.3f" 2.511)
(format "%.2f" 2.511)
(format "%.0f"  2.511)
(format "%d" 134.44)
(format "%s %s %s" 2021 11 13)

