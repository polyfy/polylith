(ns polylith.clj.core.creator.project
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.git.interface :as git]))

(defn create-project [ws-dir project-name is-git-add]
  (let [project-path (str ws-dir "/projects/" project-name)
        filename (str project-path "/deps.edn")]
    (file/create-dir project-path)
    (file/create-file filename
                      [(str "{:deps {org.clojure/clojure {:mvn/version \"1.10.1\"}")
                       (str "        org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}")
                       ""
                       (str " :aliases {:test {:extra-paths []")
                       (str "                  :extra-deps  {}}}}")])
    (git/add ws-dir filename is-git-add)))

(defn print-alias-message [project-name color-mode]
  (let [message (str "  It's recommended to add an alias to :projects in ./workspace.edn for the "
                     (color/project project-name color-mode) " project.")]
    (println message)))

(defn create [{:keys [ws-dir projects settings]} project-name is-git-add]
  (let [color-mode (:color-mode settings color/none)]
    (if (common/find-project project-name projects)
      (println (str "  Project " (color/project project-name color-mode) " (or alias) already exists."))
      (do
        (create-project ws-dir project-name is-git-add)
        :ok))))
