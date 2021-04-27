(ns goldly.app
  (:require
   ; side-effecs
   [pinkie.default-setup] ; pinkie is a necessary dependency, because goldly systems use it for frontend description   

   [goldly.puppet.subs]
   [goldly.puppet.db]

   [goldly.views.about]
   [goldly.views.system-list]
   [goldly.views.system]

   [goldly.runner.ws]
   [goldly.runner.db] ; side-effects
   [goldly.runner.clj-fn] ; side-effects
   [goldly.events]))
