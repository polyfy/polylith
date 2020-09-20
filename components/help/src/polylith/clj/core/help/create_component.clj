(ns polylith.clj.core.help.create-component)

(defn help-text []
  (str "  Creates a component.\n"
       "\n"
       "  poly create c name:NAME [interface:INTERFACE]\n"
       "    NAME = The name of the component.\n"
       "\n"
       "    INTERFACE = The name of the interface (namespace) or NAME if not given.\n"
       "\n"
       "  example:\n"
       "    poly create c name:user\n"
       "    poly create c name:admin interface:user"))

(defn print-help []
  (println (help-text)))
