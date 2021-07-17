(ns goldly.app
  (:require
   ; side-effecs
   [pinkie.default-setup] ; pinkie is a necessary dependency, because goldly systems use it for frontend description   

   [goldly.service.core]
   [goldly.broadcast.core]
   [goldly.sci.kernel-cljs]
   [goldly.cljs.loader]
   [goldly.extension.lazy]
   [goldly.component.load]
   [goldly.component.type.system]
   [goldly.component.type.notebook]

   [goldly.system.ui]
   [goldly.system.db]
   [goldly.system.ws]
   ;[goldly.system.subs]
   [goldly.events]
   ;[goldly.notebook.pinkie] ; moved to edn config
   [goldly.scratchpad.core]

   [ui.notebook.loader.list]
   [goldly.notebook-loader.clj-list]
   [goldly.notebook-loader.clj-load]

;
   ))
