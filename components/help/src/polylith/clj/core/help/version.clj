(ns ^:no-doc polylith.clj.core.help.version)

(defn help []
  (str "  poly version\n"
       "\n"
       "  Prints out:\n"
       "    - the tool name ('poly' or 'polyx')\n"
       "    - the version (major.minor.patch)\n"
       "    - the revision (SNAPSHOT if a shapshot release, otherwise empty)\n"
       "    - snapshot sequence number (if a snapshot release)\n"
       "    - the date (year-month-day)\n"
       "\n"
       "  Example of a final release:\n"
       "    poly 0.2.18 (2023-09-27)\n"
       "\n"
       "  Example of a snapshot release:\n"
       "    poly 0.2.18-SNAPSHOT #1 (2023-09-15)\n"
       "\n"
       "  The poly tool does not only version control releases but also the workspace\n"
       "  structure and the public API, which we can read more about by executing:\n"
       "    poly doc ws:version"
       ))

(defn print-help []
  (println (help)))

(comment
  (print-help)
  #__)
