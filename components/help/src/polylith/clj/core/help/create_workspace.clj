(ns ^:no-doc polylith.clj.core.help.create-workspace
     (:require [polylith.clj.core.help.shared :as s]))

(defn help-text [cm]
  (str "  Creates a workspace in current directory. If the workspace is created within\n"
       "  an existing git repo, then that repository will be used. If the workspace is\n"
       "  created outside a git repo, and the " (s/key ":create" cm) " flag is passed in, then a new repo\n"
       "  will be initiated using the 'main' branch, if another branch is not explicitly\n"
       "  given. In both cases, all the created files and directories will be committed\n"
       "  to the repo in a single commit with the text 'Workspace created.'.\n"
       "\n"
       "  If " (s/key ":commit" cm) " is not passed in, then the repository needs to be initiated manually\n"
       "  with statements similar to this:\n"
       "    git init\n"
       "    git add .\n"
       "    git commit -m \"Workspace created.\"\n"
       "\n"
       "  poly create workspace [name:" (s/key "NAME" cm) "] top-ns:" (s/key "TOP-NAMESPACE" cm) " [:commit] [branch:" (s/key "BRANCH" cm) "]\n"
       "    " (s/key "NAME" cm) " = The name of the workspace to create, which must be given\n"
       "           if created outside a git repository. Otherwise it's optional.\n"
       "\n"
       "    " (s/key "TOP-NAMESPACE" cm) " = The top namespace, e.g. com.my.company.\n"
       "\n"
       "    " (s/key "BRANCH" cm) " = The name of the branch, e.g. master. Default is main.\n"
       "\n"
       "  Example:\n"
       "    poly create w name:myws top-ns:com.my.company\n"
       "    poly create workspace name:myws top-ns:com.my.company\n"
       "    poly create workspace name:myws top-ns:com.my.company :commit\n"
       "    poly create workspace name:myws top-ns:com.my.company branch:master"))

(defn print-help [color-mode]
  (println (help-text color-mode)))

(comment
  (print-help "dark")
  #__)
