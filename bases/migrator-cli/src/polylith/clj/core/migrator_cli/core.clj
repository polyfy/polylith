(ns polylith.clj.core.migrator-cli.core
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.migrator.interface :as migrator])
  (:gen-class))

(defn ensure-dir-exists [dir]
  (when (str/blank? dir)
    (println "  Please give the workspace directory to migrate.")
    (System/exit 1))
  (when (-> dir file/exists not)
    (println "  Directory doesn't exist")
    (System/exit 1)))

(defn -main [& [from-dir]]
  (ensure-dir-exists from-dir)
  (migrator/migrate from-dir))
