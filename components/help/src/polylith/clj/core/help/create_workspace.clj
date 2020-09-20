(ns polylith.clj.core.help.create-workspace)

(defn help-text []
  (str "  Creates a workspace.\n"
       "\n"
       "  poly create w name:NAME [top-ns:TOP-NAMESPACE]\n"
       "    NAME = The name of the workspace and its directory.\n"
       "\n"
       "    TOP-NAMESPACE = The name of the top namespace.\n"
       "\n"
       "  example:\n"
       "    poly create w name:myws top-ns:com.my.company\n"
       "    poly create workspace name:myws top-ns:com.my.company"))

(defn print-help []
  (println (help-text)))
