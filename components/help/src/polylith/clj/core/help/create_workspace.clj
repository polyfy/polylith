(ns ^:no-doc polylith.clj.core.help.create-workspace
     (:require [polylith.clj.core.help.shared :as s]))

(defn help-text [cm]
  (str "  Creates a workspace in current directory. If the workspace is created within\n"
       "  an existing git repo, then that repository will be used. If the workspace is\n"
       "  created outside a git repo, then you have two alternatives:\n"
       "\n"
       "  1. Pass in " (s/key ":commit" cm) " and let the tool initiate the repository and commit the files\n"
       "     for you, using these commands:\n"
       "       git init\n"
       "       git add .\n"
       "       git commit -m \"Workspace created.\"\n"
       "\n"
       "  2. Initiate the workspace manually by executing commands similar to the ones above.\n"
       "\n"
       "  poly create workspace [name:" (s/key "NAME" cm) "] top-ns:" (s/key "TOP-NAMESPACE" cm) " [:commit] [branch:" (s/key "BRANCH" cm) "]\n"
       "    " (s/key "NAME" cm) " = The name of the workspace to create, which must be given\n"
       "           if created outside a git repository. Otherwise it's optional.\n"
       "\n"
       "    " (s/key "TOP-NAMESPACE" cm) " = The top namespace name.\n"
       "\n"
       "    " (s/key "BRANCH" cm) " = The name of the branch, or main if not given.\n"
       "\n"
       "  Example:\n"
       "    poly create w name:myws top-ns:com.my.company :commit\n"
       "    poly create workspace name:myws top-ns:com.my.company\n"
       "    poly create workspace name:myws top-ns:com.my.company :commit\n"
       "    poly create workspace name:myws top-ns:com.my.company branch:master :commit"))

(defn print-help [color-mode]
  (println (help-text color-mode)))

(comment
  (print-help "dark")
  #__)
