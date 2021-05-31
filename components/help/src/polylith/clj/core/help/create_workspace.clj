(ns polylith.clj.core.help.create-workspace
     (:require [polylith.clj.core.help.shared :as s]))

(defn help-text [cm]
  (str "  Creates a workspace in current directory. If the workspace is created within\n"
       "  an existing git repo, then that repository will be used. If the workspace is\n"
       "  created outside a git repo, then a new repo will be initiated.\n"
       "  In both cases, all the created files and directories will be committed to the\n"
       "  repo in a single commit with the text 'Workspace created.'.\n"
       "\n"
       "  poly create w [name:" (s/key "NAME" cm) "] top-ns:" (s/key "TOP-NAMESPACE" cm) "\n"
       "    " (s/key "NAME" cm) " = The name of the workspace to create, which must be given\n"
       "           if created outside a git repository. Otherwise it's optional.\n"
       "\n"
       "    " (s/key "TOP-NAMESPACE" cm) " = The top namespace, e.g. com.my.company.\n"
       "\n"
       "  Example:\n"
       "    poly create w top-ns:com.my.company\n"
       "    poly create w name:myws top-ns:com.my.company\n"
       "    poly create workspace name:myws top-ns:com.my.company"))

(defn print-help [color-mode]
  (println (help-text color-mode)))

(comment
  (print-help "dark")
  #__)
