(ns goldly.app
  (:require
   ; side-effecs
   [pinkie.default-setup] ; pinkie is a necessary dependency, because goldly systems use it for frontend description   

   [goldly.puppet.subs]
   [goldly.puppet.db]

   [goldly.views.about]
   [goldly.views.system-list]
   [goldly.views.system]

   [goldly.events]))
