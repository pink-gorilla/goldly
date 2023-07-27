(ns demo.notebook.sciload
  (:require [demo.notebook.willy :refer [saying] :as bongo]))

(saying)
(bongo/saying)
(demo.notebook.willy/saying)

(require '[demo.notebook.willy :refer [saying]])

(saying)