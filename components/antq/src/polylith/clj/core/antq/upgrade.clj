(ns ^:no-doc polylith.clj.core.antq.upgrade
  (:require [antq.api :as antq]
            [polylith.clj.core.util.interface.color :as color]))

(defn dev? [type name]
  (and (= type "project")
       (= name "development")))

(defn file-path [ws-dir type name lib-type]
  (let [filename (if (= "npm" lib-type) "package.json" "deps.edn")]
    (if (dev? type name)
      (str ws-dir "/" filename)
      (str ws-dir "/" type "s/" name "/" filename))))

(defn upgrade-lib [ws-dir color-mode entity-type entity-name [lib-name {:keys [version latest-version type]}]]
  (let [lib-type (or type "maven")
        file-path (file-path ws-dir entity-type entity-name lib-type)
        message (str lib-name " to " latest-version " in " (color/entity entity-type entity-name color-mode) ".")]
    (if (get (antq/upgrade-deps! [{:file file-path
                                   :dependency {:project (if (= "npm" lib-type) :npm :clojure)
                                                :type (if (= "npm" lib-type) :npm :java)
                                                :name lib-name
                                                :version version
                                                :latest-version latest-version}}])
             true)
     (println (str "  Updated " message))
     (println (str "  Failed to update " message)))))
