(ns ui.goldly.code.core
  (:require
   [re-frame.core :as rf]
   [pinkie.pinkie :refer-macros [register-component]]
   [ui.goldly.code.viewer :refer [code-viewer]]
   [ui.goldly.code.css :as css]))

(register-component :p/code code-viewer)

(rf/dispatch [:css/add-components css/components css/config])