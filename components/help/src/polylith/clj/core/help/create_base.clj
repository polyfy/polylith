(ns polylith.clj.core.help.create-base)

(defn help-text []
  (str "  Creates a base.\n"
       "\n"
       "  poly create b name:NAME\n"
       "    NAME = The name of the base.\n"
       "\n"
       "  example:\n"
       "    poly create b name:mybase\n"
       "    poly create base name:mybase"))

(defn print-help []
  (println (help-text)))
