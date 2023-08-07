(ns ^:no-doc polylith.clj.core.doc.interface
  (:require [polylith.clj.core.doc.core :as core]
            [polylith.clj.core.doc.navigation :as navigation]))

(def command-nav navigation/command-nav)
(def more-nav core/more-navigation)
(def pages-nav navigation/pages-nav)
(def ws-nav navigation/ws-nav)

(defn open-doc [cmd command more page ws]
  (core/open-doc cmd command more page ws))
