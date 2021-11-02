(ns goldly.app-system
  (:require
   [goldly.component.load]
   [goldly.component.type.system]
   [goldly.component.type.notebook]

   [goldly.notebook-loader.clj-load]
   [goldly.notebook-loader.clj-list :refer [start-watch-notebooks]]

   [ui.notebook.loader.list]
   [goldly.broadcast.core]
   ; needs refactoring:
   [goldly.system.ui]
   [goldly.system.db]
   [goldly.system.ws]
   [goldly.scratchpad.core]))



