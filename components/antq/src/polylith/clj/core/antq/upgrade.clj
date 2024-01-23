(ns ^:no-doc polylith.clj.core.antq.upgrade
  (:require [antq.api :as antq]
            [polylith.clj.core.util.interface.color :as color]))

(defn dev? [type name]
  (and (= type "project")
       (= name "development")))

(defn file-path [ws-dir type name]
  (if (dev? type name)
    (str ws-dir "/deps.edn")
    (str ws-dir "/" type "s/" name "/deps.edn")))

(defn upgrade-lib [ws-dir color-mode entity-type entity-name [lib-name {:keys [version latest-version]}]]
  (let [file-path (file-path ws-dir entity-type entity-name)
        message (str lib-name " to " latest-version " in " (color/entity entity-type entity-name color-mode) ".")]
    (if (get (antq/upgrade-deps! [{:file file-path
                                   :dependency {:project :clojure
                                                :type :java
                                                :name lib-name
                                                :version version
                                                :latest-version latest-version}}])
             true)
     (println (str "  Updated " message))
     (println (str "  Failed to update " message)))))
