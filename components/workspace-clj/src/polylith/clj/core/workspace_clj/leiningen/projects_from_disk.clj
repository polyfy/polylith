(ns polylith.clj.core.workspace-clj.leiningen.projects-from-disk
  (:require [clojure.string :as str]
            [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.workspace-clj.leiningen.config-from-disk :as lein-config-from-disk]))

(defn absolute-path [path project-name]
  (cond
    (str/starts-with? path "./") (str "projects/" project-name "/" (subs path 2))
    (str/starts-with? path "../../") (subs path 6)
    :else (str "projects/" project-name "/" path)))

(defn brick? [[_ {:keys [local/root]}]]
  (and (-> root nil? not)
       (or (str/starts-with? root "../../bases/")
           (str/starts-with? root "../../components/"))))

(defn ->deps-and-paths [entry project-name project-dir]
  (let [path (-> entry second :local/root)
        brick-path (absolute-path path project-name)
        config (read-string (slurp (str project-dir "/" path "/deps.edn")))
        src-paths (:paths config)
        test-paths (-> config :aliases :test :extra-paths)
        src-deps (:deps config)
        test-deps (-> config :aliases :test :extra-deps)]
    {:src-paths (mapv #(str brick-path "/" %) src-paths)
     :test-paths (mapv #(str brick-path "/" %) test-paths)
     :src-deps src-deps
     :test-deps test-deps}))

(def config {:deps {'clj-time #:mvn{:version "0.14.4"}, 'org.clojure/clojure #:mvn{:version "1.9.0"}}})



(defn read-project
  ([{:keys [project-name project-dir is-dev]} ws-type user-home color-mode]
   (let [[ok? config] (common/leiningen-config-key project-dir :dependencies)]))

         ;{:keys [paths deps aliases mvn/repos] :as config} (read-string (slurp config-filename))
         ; todo: fix message (when (not is-dev) (validator/validate-project-deployable-config ws-type config))]
     ;(if message
     ;  (throw (ex-info (str "  " (color/error color-mode (str "Error in " project-dir "/project.clj: ") message)) message))
     ;  (read-project project-name project-dir ws-type is-dev paths deps aliases user-home)))
  ([project-name project-dir ws-type is-dev paths deps aliases user-home]
   (let [toolsdeps1? (= :toolsdeps1 ws-type)
         deps-and-paths (map #(->deps-and-paths % project-name project-dir) (filter brick? deps))
         src-paths (vec (sort (set (if is-dev (-> aliases :dev :extra-paths)
                                              (if toolsdeps1? (map #(absolute-path % project-name) paths)
                                                              (mapcat :src-paths deps-and-paths))))))
         lib-deps {:src (lib/with-sizes (if is-dev (-> aliases :dev :extra-deps)
                                                   (concat (filter (complement brick?) deps)
                                                           (mapcat :src-deps deps-and-paths))) user-home)
                   :test (lib/with-sizes (concat (-> aliases :test :extra-deps)
                                                 (mapcat :test-deps deps-and-paths)) user-home)}
         test-paths (-> aliases :test :extra-paths)
         absolute-test-paths (vec (sort (set (if is-dev test-paths
                                                        (if toolsdeps1? (mapv #(absolute-path % project-name) test-paths)
                                                                        (concat (mapcat :test-paths deps-and-paths)
                                                                                (filter #(str/starts-with? % "projects/")
                                                                                        (map #(absolute-path % project-name) test-paths))))))))
         namespaces (ns-from-disk/namespaces-from-disk (str project-dir "/src") (str project-dir "/test"))]
     (util/ordered-map :name project-name
                       :is-dev is-dev
                       :project-dir project-dir
                       ;:config-filename config-filename
                       :type "project"
                       :src-paths src-paths
                       :test-paths absolute-test-paths
                       :lib-deps lib-deps
                       :maven-repos mvn/standard-repos
                       :namespaces namespaces))))

(defn project-map [ws-dir project-name]
  {:project-name project-name
   :is-dev false
   :project-dir (str ws-dir "/systems/" project-name)})

(defn read-projects [ws-dir ws-type user-home color-mode]
  (let [project-configs (conj (map #(project-map ws-dir %)
                                   (file/directories (str ws-dir "/projects")))
                              {:project-name "development"
                               :is-dev true
                               :project-dir (str ws-dir "/environments/development")})]
    (mapv #(read-project % ws-type user-home color-mode) project-configs)))

  ;; todo: add the top level key :paths to the ws structure.

