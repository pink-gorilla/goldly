(ns goldly.app
  (:require
   ; side-effecs
   [pinkie.default-setup] ; pinkie is a necessary dependency, because goldly systems use it for frontend description   

   [goldly.service.core]
   [goldly.broadcast.core]
   [goldly.sci.kernel-cljs]
   [goldly.notebook.pinkie]

   [goldly.puppet.subs]
   [goldly.puppet.db]
   [goldly.runner.ws]
   [goldly.system]
   [goldly.system.db]
   [goldly.events]))
