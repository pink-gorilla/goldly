(ns systems.python-svm
  (:require
   [goldly.runner :refer [system-start!]]
   [goldly.system :as goldly]))

; ported from:
; https://github.com/plotly/dash-svm/blob/master/app.py