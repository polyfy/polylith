(ns polylith.clj.core.create.workspace
  (:require [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.shell.interfc :as shell]))

(defn create [ws-path ws-name ws-ns]
  (file/create-dir (str ws-path "/" ws-name))
  (file/create-dir (str ws-path "/" ws-name "/bases"))
  (file/create-dir (str ws-path "/" ws-name "/components"))
  (file/create-dir (str ws-path "/" ws-name "/environments"))
  (file/create-file (str ws-path "/" ws-name "/deps.edn")
                    [(str "{:polylith {:vcs \"git\"")
                     (str "            :color-mode \"dark\"")
                     (str "            :top-namespace \"" ws-ns "\"")
                     (str "            :env-short-names {}}}")])
  (try
    (shell/sh "git" "init" :dir ws-path)
    (shell/sh "git" "add" "." :dir ws-path)
    (shell/sh "git" "commit" "-m" "Initial commit." :dir ws-path)
    (catch Exception _
      (println (str "Cannot create a git repository for the workspace.\n"
                    "Please try to create it manually instead.")))))
