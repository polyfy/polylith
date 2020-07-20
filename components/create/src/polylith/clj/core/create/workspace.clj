(ns polylith.clj.core.create.workspace
  (:require [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.git.interfc :as git]))

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
  (git/init ws-path))
