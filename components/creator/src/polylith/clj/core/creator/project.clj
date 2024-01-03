(ns ^:no-doc polylith.clj.core.creator.project
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.creator.shared :as shared]
            [clojure.string :as str]))

(defn next-alias-number [aliases]
  (first (drop-while #(contains? aliases (str "?" %))
                     (range 1 999))))

(defn create-project [ws-dir project-name aliases is-git-add]
  (let [project-path (str ws-dir "/projects/" project-name)
        config-filename (str project-path "/config.edn")
        deps-filename (str project-path "/deps.edn")]
    (file/create-dir project-path)
    (file/create-file config-filename
                      [(str "{:alias \"?" (next-alias-number aliases) "\"}")])
    (file/create-file deps-filename
                      [(str "{:deps {org.clojure/clojure {:mvn/version \"" shared/clojure-ver "\"}}")
                       ""
                       (str " :aliases {:test {:extra-paths []")
                       (str "                  :extra-deps  {}}}}")])
    (git/add ws-dir config-filename is-git-add)
    (git/add ws-dir deps-filename is-git-add)))

(defn print-alias-message [project-name color-mode]
  (let [message (str "  It's recommended to rename the alias in config.edn for the "
                     (color/project project-name color-mode) " project.")]
    (println message)))

(defn validate [project-name projects color-mode]
  (cond
    (str/blank? project-name) [false "  A project name must be given."]
    (common/find-project project-name projects) [false (str "  Project " (color/project project-name color-mode) " (or alias) already exists.")]
    :else [true]))

(defn create [{:keys [ws-dir projects settings]} project-name is-git-add]
  (let [color-mode (:color-mode settings color/none)
        aliases (set (map :alias projects))
        [ok? message] (validate project-name projects color-mode)]
    (if (not ok?)
      (println message)
      (do
        (create-project ws-dir project-name aliases is-git-add)
        :ok))))
