(ns polylith.clj.core.migrator.core
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn next-ws-dir [from-ws-dir]
  (util/find-first #(-> % file/exists not)
                   (map #(str from-ws-dir (format "-%02d" %))
                        (range 1 100))))

(defn config-key [key path]
  (let [content (read-string (slurp (str path "/project.clj")))
        index (ffirst
                (filter #(= key (second %))
                        (map-indexed vector content)))]
    (when index
      (nth content (inc index)))))

(defn source-paths [from-dir bricks-dir brick-name n#spaces dirs subdir]
  (mapv #(str (str-util/spaces n#spaces) "\"" subdir bricks-dir "/" brick-name "/" % "\"")
        (filterv #(contains? dirs %)
                 (file/directories (str from-dir "/" bricks-dir "/" brick-name)))))

(defn src-dev-paths [from-dir bricks-dir brick-name]
  (source-paths from-dir bricks-dir brick-name 32 #{"src" "resources"} ""))

(defn test-dev-paths [from-dir bricks-dir brick-name]
  (source-paths from-dir bricks-dir brick-name 33 #{"test"} ""))

(defn src-project-paths [from-dir bricks-dir brick-name]
  (source-paths from-dir bricks-dir brick-name 11 #{"src" "resources"} "../../"))

(defn test-project-paths [from-dir bricks-dir brick-name]
  (source-paths from-dir bricks-dir brick-name 32 #{"test"} "../../"))


(defn lib-row [[lib version & params] n#spaces]
  (let [exclusions (if-let [index (first (util/find-first #(= :exclusions (second %)) (map-indexed vector params)))]
                     (str ", :exclusions " (nth (vec params) (inc index)))
                     "")]
    (str (str-util/spaces n#spaces) lib " {:mvn/version \"" version "\"" exclusions "}")))

(defn alias-row [[project-name alias]]
  (if (= "development" project-name)
    (str "                               \"" project-name "\" {:alias \"" alias "\", :test []}")
    (str "                               \"" project-name "\" {:alias \"" alias "\"")))

(defn project-configs [system-names]
  (let [aliases (sort-by first (conj (map #(vector % %) system-names) ["development" "dev"]))]
    (concat
      [(str "            :projects {")]
      (map alias-row aliases)
      [(str "                              }")])))

(defn workspace-content [top-ns system-names]
  (concat
    [(str "")
     (str "{:polylith {:top-namespace \"" top-ns "\"")
     (str "            :interface-ns \"interface\"")
     (str "            :default-profile-name \"default\"")
     (str "            :compact-views #{}")
     (str "            :vcs {:name \"git\"")
     (str "                  :auto-add false}")
     (str "            :tag-patterns {:stable \"stable-*\"")
     (str "                           :release \"v[0-9]*\"}")]
    (project-configs system-names)
    [(str "}")]))

(defn dev-deps-content [from-dir component-names base-names libraries]
  (concat
    [(str " :aliases  {:dev {:extra-paths [\"development/src\"")]
    (mapcat #(src-dev-paths from-dir "components" %) component-names)
    (mapcat #(src-dev-paths from-dir "bases" %) base-names)
    [(str "                               ]")
     (str "                  :extra-deps {")]
    (mapv #(lib-row % 31) libraries)
    [(str "                              }}")
     (str "            :test {:extra-paths [")]
    (mapcat #(test-dev-paths from-dir "components" %) component-names)
    (mapcat #(test-dev-paths from-dir "bases" %) base-names)
    [(str "                                 ]}")
     (str "")
     (str "            :poly {:main-opts [\"-m\" \"polylith.clj.core.poly-cli.core\"]")
     (str "                   :extra-deps {polyfy/polylith")
     (str "                                {:git/url   \"https://github.com/polyfy/polylith.git\"")
     (str "                                 :sha       \"LATEST\"")
     (str "                                 :deps/root \"projects/poly\"}}}}}")]))

(defn project-deps-content [from-dir component-names base-names libraries]
  (concat
    [(str "")
     (str "{:paths   [")]
    (mapcat #(src-project-paths from-dir "components" %) component-names)
    (mapcat #(src-project-paths from-dir "bases" %) base-names)
    [(str "                               ]")
     (str " :deps    {")]
    (mapv #(lib-row % 11) libraries)
    [(str "          }")
     (str " :aliases {:test {:extra-paths [")]
    (mapcat #(test-project-paths from-dir "components" %) component-names)
    (mapcat #(test-project-paths from-dir "bases" %) base-names)
    [(str "                                 ]}}}")]))

(defn create-workspace [to-dir top-ns system-names]
  (file/create-file (str to-dir "/workspace.edn")
                    (workspace-content top-ns system-names)))

(defn create-dev [from-dir to-dir top-ns component-names base-names]
  (let [dev-brick-names (map common/path-to-ns (file/directories (str from-dir "/environments/development/src/" (common/ns-to-path top-ns))))
        dev-component-names (sort (filter #(contains? (set component-names) %) dev-brick-names))
        dev-base-names (sort (filter #(contains? (set base-names) %) dev-brick-names))
        libs (sort-by #(-> % first str) (config-key :dependencies (str from-dir "/environments/development")))]
    (file/create-file (str to-dir "/deps.edn")
                      (dev-deps-content from-dir dev-component-names dev-base-names libs))))

(defn create-project [from-dir to-dir project-name top-ns component-names base-names]
  (let [project-brick-names (map common/path-to-ns (file/directories (str from-dir "/systems/" project-name "/src/" (common/ns-to-path top-ns))))
        project-component-names (sort (filter #(contains? (set component-names) %) project-brick-names))
        project-base-names (sort (filter #(contains? (set base-names) %) project-brick-names))
        libs (sort-by #(-> % first str) (config-key :dependencies (str from-dir "/systems/" project-name)))]
    (file/create-dir (str to-dir "/projects/" project-name))
    (file/create-file (str to-dir "/projects/" project-name "/deps.edn")
                      (project-deps-content from-dir project-component-names project-base-names libs))))

(defn copy-brick [from-dir to-dir brick-name bricks-dir]
  (let [from-path (str from-dir "/" bricks-dir "/" brick-name "/")
        to-path (str to-dir "/" bricks-dir "/" brick-name "/")
        from-path-cnt (count from-path)
        from-files (filter #(file/exists %)
                           (map #(str from-path %)
                                ["src" "resources" "test" "readme.md"]))
        filenames (mapv #(subs % from-path-cnt) from-files)]
    (doseq [filename filenames]
      (file/copy-file-or-dir+ (str from-path "/" filename) (str to-path "/" filename)))))

(defn copy-bricks [from-dir to-dir brick-names bricks-dir]
  (doseq [brick-name brick-names]
    (copy-brick from-dir to-dir brick-name bricks-dir)))

(defn migrate [from-dir]
  (let [to-dir (next-ws-dir from-dir)
        top-ns (-> :polylith (config-key from-dir) :top-namespace)
        component-names (sort (file/directories (str from-dir "/components")))
        base-names (sort (file/directories (str from-dir "/bases")))
        system-names (file/directories (str from-dir "/systems"))]
    (file/create-dir to-dir)
    (copy-bricks from-dir to-dir component-names "components")
    (copy-bricks from-dir to-dir base-names "bases")
    (file/create-dir (str to-dir "/development"))
    (file/create-dir (str to-dir "/projects"))
    (file/create-dir (str to-dir "/development/src"))
    (file/create-file (str to-dir "/development/src/.keep") [""])
    (create-workspace to-dir top-ns system-names)
    (create-dev from-dir to-dir top-ns component-names base-names)
    (doseq [system-name system-names]
      (create-project from-dir to-dir system-name top-ns component-names base-names))
    (println (str "  Successfully migrated to: " to-dir))))
