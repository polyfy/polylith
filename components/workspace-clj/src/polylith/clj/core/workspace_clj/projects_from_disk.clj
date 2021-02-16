(ns polylith.clj.core.workspace-clj.projects-from-disk
  (:require [clojure.string :as str]
            [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.validator.interface :as validator]))

(defn absolute-path [path project-name]
  (cond
    (str/starts-with? path "./") (str "projects/" project-name "/" (subs path 2))
    (str/starts-with? path "../../") (subs path 6)
    :else (str "projects/" project-name "/" path)))

(defn absolute-paths [project-name paths dev-project?]
  (let [sorted-paths (-> paths sort vec)]
    (if dev-project?
      sorted-paths
      (mapv #(absolute-path % project-name) sorted-paths))))

(defn read-project
  ([{:keys [project-name project-dir config-file is-dev]} user-home color-mode]
   (let [{:keys [paths deps aliases mvn/repos] :as config} (read-string (slurp config-file))
         maven-repos (merge mvn/standard-repos repos)
         message (when (not is-dev) (validator/validate-deployable-config config))]
     (if message
       (throw (ex-info (str "  " (color/error color-mode (str "Error in " config-file ": ") message)) message))
       (read-project project-name project-dir config-file is-dev paths deps aliases maven-repos user-home))))
  ([project-name project-dir config-file is-dev paths deps aliases maven-repos user-home]
   (let [src-paths (if is-dev (-> aliases :dev :extra-paths) paths)
         lib-deps (lib/with-sizes (if is-dev (-> aliases :dev :extra-deps) deps) user-home)
         lib-deps-test (lib/with-sizes (-> aliases :test :extra-deps) user-home)
         absolute-src-paths (absolute-paths project-name src-paths is-dev)
         test-paths (-> aliases :test :extra-paths)
         absolute-test-paths (absolute-paths project-name test-paths is-dev)
         namespaces-src (ns-from-disk/namespaces-from-disk (str project-dir "/src"))
         namespaces-test (ns-from-disk/namespaces-from-disk (str project-dir "/test"))]
     (util/ordered-map :name project-name
                       :is-dev is-dev
                       :project-dir project-dir
                       :config-file config-file
                       :type "project"
                       :src-paths absolute-src-paths
                       :test-paths absolute-test-paths
                       :lib-deps lib-deps
                       :lib-deps-test lib-deps-test
                       :maven-repos maven-repos
                       :namespaces-src namespaces-src
                       :namespaces-test namespaces-test))))

(defn project-map [ws-dir project-name]
  {:project-name project-name
   :is-dev false
   :project-dir (str ws-dir "/projects/" project-name)
   :config-file (str ws-dir "/projects/" project-name "/deps.edn")})

(defn read-projects [ws-dir user-home color-mode]
  (let [project-configs (conj (map #(project-map ws-dir %)
                                   (file/directories (str ws-dir "/projects")))
                              {:project-name "development"
                               :is-dev true
                               :project-dir (str ws-dir "/development")
                               :config-file (str ws-dir "/deps.edn")})]
    (mapv #(read-project % user-home color-mode) project-configs)))
