(ns ^:no-doc polylith.clj.core.creator.project
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.creator.shared :as shared]
            [polylith.clj.core.creator.template :as template]
            [clojure.string :as str]))

(defn ->data [project-name dialect template-data]
  (merge {:clojure-ver shared/clojure-ver
          :project-name project-name
          :shadow-cljs-ver shared/shadow-cljs-ver
          :dialect dialect}
         template-data))

(defn add-deps-config-file [ws-dir project-path data is-git-add]
  (let [filename (str project-path "/deps.edn")]
    (file/create-file filename
                      [(template/render ws-dir "projects/deps.edn" data)])
    (git/add ws-dir filename is-git-add)))

(defn add-npm-config-file [ws-dir project-path data is-git-add]
  (let [filename (str project-path "/package.json")]
    (file/create-file filename
                      [(template/render ws-dir "projects/package.json" data)])
    (git/add ws-dir filename is-git-add)))

(defn create-project [ws-dir project-name template-data dialect is-git-add]
  (let [project-path (str ws-dir "/projects/" project-name)
        data (->data project-name dialect template-data)]
    (file/create-dir project-path)
    (add-deps-config-file ws-dir project-path data is-git-add)
    (when (= "cljs" dialect)
      (add-npm-config-file ws-dir project-path data is-git-add))))

(defn print-alias-message [project-name project-alias color-mode]
  (when (nil? project-alias)
    (let [message (str "  It's recommended to add an alias to :projects in ./workspace.edn for the "
                       (color/project project-name color-mode) " project.")]
      (println message))))

(defn validate [project-name projects color-mode]
  (cond
    (str/blank? project-name) [false "  A project name must be given."]
    (common/find-project project-name projects) [false (str "  Project " (color/project project-name color-mode) " (or alias) already exists.")]
    :else [true]))

(defn create [{:keys [ws-dir projects settings configs]} project-name dialect is-git-add]
  (let [color-mode (:color-mode settings color/none)
        template-data (-> configs :workspace :template-data)
        [ok? message] (validate project-name projects color-mode)]
    (if (not ok?)
      (println message)
      (do
        (create-project ws-dir project-name template-data dialect is-git-add)
        :ok))))
