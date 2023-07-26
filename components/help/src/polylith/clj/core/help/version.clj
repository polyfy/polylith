(ns ^:no-doc polylith.clj.core.help.version
  (:require [polylith.clj.core.version.interface :as version]))

(defn help []
  (str "  poly version\n"
       "\n"
       "  Prints out the version and release date (yyyy-mm-dd) of this tool, e.g.:\n"
       "    " version/name " (" version/date ")."))

(defn print-help []
  (println (help)))

(comment
  (print-help)
  #__)
