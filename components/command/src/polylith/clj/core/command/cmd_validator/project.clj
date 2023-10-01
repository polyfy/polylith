(ns ^:no-doc polylith.clj.core.command.cmd-validator.project
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.util.interface.color :as color]))

(defn validate [selected-projects projects color-mode]
  (let [missing (set/difference selected-projects
                                (set (mapcat (juxt :alias :name) projects)))
        s (if (= 1 (count missing)) "" "s")
        missing-msg (color/project (str/join ", " missing) color-mode)]
    (when (-> missing empty? not)
      [(str "  Can't find project" s ": " missing-msg)])))
