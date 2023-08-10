(ns ^:no-doc polylith.clj.core.doc.interface
  (:require [polylith.clj.core.doc.core :as core]
            [polylith.clj.core.doc.navigation.more :as more-nav]
            [polylith.clj.core.doc.navigation.generated :as nav]))

(def help-nav nav/help-nav)
(def more-nav more-nav/navigation)
(def pages-nav nav/pages-nav)
(def ws-nav nav/ws-nav)

(defn open-doc [branch local? help more page ws unnamed-args]
  (core/open-doc branch local? help more page ws unnamed-args))
