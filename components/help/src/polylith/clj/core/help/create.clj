(ns polylith.clj.core.help.create
  (:require [polylith.clj.core.help.create-component :as component]
            [polylith.clj.core.help.create-base :as base]
            [polylith.clj.core.help.create-environment :as environment]
            [polylith.clj.core.help.create-workspace :as workspace]))

(defn help-text []
  (str "  Creates a component, base, environment or workspace.\n"
       "\n"
       "  poly create TYPE [ARGS]\n"
       "    TYPE = c -> Creates a component.\n"
       "           b -> Creates a base.\n"
       "           e -> Creates an environment.\n"
       "           w -> Creates a workspace.\n"
       "\n"
       "  The values 'component', 'base', 'environment' and 'workspace'\n"
       "  can be used for TYPE as well as 'c', 'b', 'e' and 'w'.\n"
       "\n"
       "  To get help for a specific TYPE, write:\n"
       "    poly help create TYPE\n"
       "\n"
       "  example:\n"
       "    poly create c name:user\n"
       "    poly create component name:user\n"
       "    poly create c name:admin interface:user\n"
       "    poly create b name:mybase\n"
       "    poly create e name:myenv\n"
       "    poly create w name:myws top-ns:com.my.company"))

(defn print-help [ent]
  (case ent
    "c" (component/print-help)
    "component" (component/print-help)
    "b" (base/print-help)
    "base" (base/print-help)
    "e" (environment/print-help)
    "entity" (environment/print-help)
    "w" (workspace/print-help)
    "workspace" (workspace/print-help)
    (println (help-text))))
