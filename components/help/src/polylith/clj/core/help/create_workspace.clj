(ns polylith.clj.core.help.create-workspace
     (:require [polylith.clj.core.help.shared :as s]))

(defn help-text [cm]
  (str "  Creates a workspace in current directory. If the workspace is created within\n"
       "  an existing git repo, then that repository will be used. If the workspace is\n"
       "  created outside a git repo, then a new repo will be initiated using the 'main'\n"
       "  branch, if another branch is not explicitly given.\n"
       "  In both cases, all the created files and directories will be committed to the\n"
       "  repo in a single commit with the text 'Workspace created.'.\n"
       "\n"
       "  poly create workspace [name:" (s/key "NAME" cm) "] top-ns:" (s/key "TOP-NAMESPACE" cm) " [branch:" (s/key "BRANCH" cm) "]\n"
       "    " (s/key "NAME" cm) " = The name of the workspace to create, which must be given\n"
       "           if created outside a git repository. Otherwise it's optional.\n"
       "\n"
       "    " (s/key "TOP-NAMESPACE" cm) " = The top namespace, e.g. com.my.company.\n"
       "\n"
       "    " (s/key "BRANCH" cm) " = The name of the branch, e.g. master.\n"
       "\n"
       "  Example:\n"
       "    poly create w name:myws top-ns:com.my.company\n"
       "    poly create workspace name:myws top-ns:com.my.company\n"
       "    poly create workspace name:myws top-ns:com.my.company branch:master"))

(defn print-help [color-mode]
  (println (help-text color-mode)))

(comment
  (print-help "dark")
  #__)
