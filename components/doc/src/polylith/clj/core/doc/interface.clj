(ns ^:no-doc polylith.clj.core.doc.interface
  (:require [polylith.clj.core.doc.core :as core]
            [polylith.clj.core.doc.navigation :as navigation]))

(def help-nav navigation/help-nav)
(def more-nav core/more-navigation)
(def pages-nav navigation/pages-nav)
(def ws-nav navigation/ws-nav)

(defn open-doc [branch local? help more page ws unnamed-args]
  (core/open-doc branch local? help more page ws unnamed-args))
