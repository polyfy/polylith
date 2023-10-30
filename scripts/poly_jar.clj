(ns poly-jar
  "Script support for building poly and polyx jars"
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [lread.status-line :as status]
            [shell :refer [shell]]))

(defn build [project]
  (status/line :head "Building %s jar" project)
  ;; it takes a while to build these jars, so try to do some intelligent skipping if they already seem built
  (if (seq (fs/modified-since (format "projects/%s/target/%s.jar" project project)
                              (-> ["bb.edn" "build.clj" "deps.edn" "workspace.edn"]
                                  (into (fs/glob "bases" "**/*"))
                                  (into (fs/glob "build" "**/*"))
                                  (into (fs/glob "components" "**/*"))
                                  (into (fs/glob "projects" "**/*"))
                                  (->> (filter fs/regular-file?)
                                       (remove (fn [path] (str/ends-with? (str path) "navigation/generated.clj")))
                                       (remove (fn [path] (str/includes? (str path) "/target/")))))))
    (shell "clojure -T:build uberjar :project" project)
    (status/line :detail "Skipping building %s jar, it seems up to date." project)))
