(ns goldly.app
  (:require
   ; side-effecs
   [pinkie.default-setup] ; pinkie is a necessary dependency, because goldly systems use it for frontend description   

   [goldly.service.core]
   [goldly.broadcast.core]
   [goldly.sci.kernel-cljs]
   [goldly.notebook.pinkie]

   [goldly.system]
   [goldly.system.db]
   [goldly.system.ws]
   [goldly.system.subs]

   [goldly.events]))
