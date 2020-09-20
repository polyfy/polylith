(ns polylith.clj.core.help.create-environment)

(defn help-text []
  (str "  Creates an environment.\n"
       "\n"
       "  poly create e name:NAME\n"
       "    NAME = The name of the environment.\n"
       "\n"
       "  example:\n"
       "    poly create e name:myenv\n"
       "    poly create environment name:myenv"))

(defn print-help []
  (println (help-text)))
