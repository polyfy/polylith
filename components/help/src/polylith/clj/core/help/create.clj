(ns ^:no-doc polylith.clj.core.help.create
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.help.shared :as s]
            [polylith.clj.core.help.create-component :as component]
            [polylith.clj.core.help.create-base :as base]
            [polylith.clj.core.help.create-project :as project]
            [polylith.clj.core.help.create-workspace :as workspace]))

(defn help-text [cm]
  (str "  Creates a component, base, project or workspace.\n"
       "\n"
       "  poly create " (s/key "TYPE" cm) " [" (s/key "ARGS" cm) "]\n"
       "\n"
       "    " (s/key "TYPE" cm) " = " (s/key "component" cm) " -> Creates a component.\n"
       "           " (s/key "base     " cm) " -> Creates a base.\n"
       "           " (s/key "project  " cm) " -> Creates a project.\n"
       "           " (s/key "workspace" cm) " -> Creates a workspace.\n"
       "\n"
       "    " (s/key "ARGS" cm) " = Varies depending on " (s/key "TYPE" cm) ".\n"
       "\n"
       "  To get help for a specific " (s/key "TYPE" cm) ", execute 'poly help create " (s/key "TYPE" cm) "'.\n"
       "\n"
       "  Example:\n"
       "    poly create component name:user\n"
       "    poly create component name:user dialect:cljs\n"
       "    poly create component name:admin interface:user\n"
       "    poly create base name:mybase\n"
       "    poly create base name:mybase dialect:cljs\n"
       "    poly create project name:myproject\n"
       "    poly create project name:myproject dialect:cljs\n"
       "    poly create workspace name:myws top-ns:com.my.company\n"
       "    poly create workspace name:myws top-ns:com.my.company dialects:clj\n"
       "    poly create workspace name:myws top-ns:com.my.company dialects:clj:cljs\n"
       "    poly create workspace name:myws top-ns:com.my.company branch:master"))

(defn print-help [entity color-mode]
  (case (common/entity->short entity)
    "c" (component/print-help color-mode)
    "b" (base/print-help color-mode)
    "p" (project/print-help color-mode)
    "w" (workspace/print-help color-mode)
    (println (help-text color-mode))))

(comment
  (print-help nil "dark")
  #__)
