(ns goldly.app
  (:require
   ; side-effecs
   [pinkie.default-setup] ; pinkie is a necessary dependency, because goldly systems use it for frontend description   
   [goldly.puppet.subs]
   [goldly.puppet.db]
   [goldly.system]
   [goldly.runner.ws]
   [goldly.runner.db]
   [goldly.events]
   [goldly.sci.kernel-cljs]
   [goldly.notebook.pinkie]
   [goldly.service.core]
   [goldly.broadcast.core]
   ;[goldly.runner.clj-fn]
   ))
