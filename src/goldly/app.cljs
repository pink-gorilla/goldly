(ns goldly.app
  (:require
   ; side-effecs
   [pinkie.default-setup] ; pinkie is a necessary dependency, because goldly systems use it for frontend description   

   [goldly.puppet.subs]
   [goldly.puppet.db]
   [goldly.system]
   [goldly.runner.ws]
   [goldly.runner.db]
   [goldly.runner.clj-fn]
   [goldly.events]
   [goldly.sci.kernel-cljs]
   [goldly.notebook.pinkie]
   [goldly.service.core]))
