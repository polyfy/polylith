(ns ^:no-doc polylith.clj.core.antq.upgrade
  (:require [antq.api :as antq]
            [polylith.clj.core.util.interface.color :as color]))

(defn upgrade-lib [ws-dir color-mode entity-type entity-name [lib-name {:keys [version latest-version]}]]
  (let [filename (str ws-dir "/" entity-type "s/" entity-name "/deps.edn")
        message (str lib-name " to " latest-version " in " (color/entity entity-type entity-name color-mode) ".")]
    (if (get (antq/upgrade-deps! [{:file filename
                                   :dependency {:project :clojure
                                                :type :java
                                                :name lib-name
                                                :version version
                                                :latest-version latest-version}}])
             true)
     (println (str "  Updated " message))
     (println (str "  Failed to update " message)))))
