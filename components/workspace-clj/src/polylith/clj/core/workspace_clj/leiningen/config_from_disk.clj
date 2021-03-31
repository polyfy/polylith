(ns polylith.clj.core.workspace-clj.leiningen.config-from-disk
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]))

(defn maven [[lib version]]
  [lib {:mvn/version version}])

(defn not-interfaces? [[lib _]]
  (not (str/ends-with? (str lib) "/interfaces")))

(defn read-config-file [brick-dir]
  (let [config-file (str brick-dir "/project.clj")]
    (if (-> config-file file/exists not)
      [false (str "Could not find config file: " config-file)]
      [true {:deps (into {} (map maven (filter not-interfaces?
                                               (common/leiningen-config-key brick-dir :dependencies))))}])))
