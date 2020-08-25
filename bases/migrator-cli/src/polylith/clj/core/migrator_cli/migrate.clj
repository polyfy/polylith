(ns polylith.clj.core.migrator-cli.migrate
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.migrator.interfc :as migrator])
  (:gen-class))

(defn ensure-dir-exists [dir]
  (when (str/blank? dir)
    (println "Please give the workspace directory to migrate.")
    (System/exit 1))
  (when (-> dir file/exists not)
    (println "Directory doesn't exist")
    (System/exit 1)))

(defn -main [& [from-dir]]
  (ensure-dir-exists from-dir)
  (migrator/migrate from-dir))
