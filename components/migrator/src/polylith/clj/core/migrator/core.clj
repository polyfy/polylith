(ns polylith.clj.core.migrator.core
  (:require [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.str :as str-util]))

(defn next-ws-dir [from-ws-dir]
  (util/find-first #(-> % file/exists not)
                   (map #(str from-ws-dir (format "-%02d" %))
                        (range 1 100))))

(defn copy-dir [from-dir to-dir dir]
  (file/copy-dir (str from-dir "/" dir) (str to-dir "/" dir)))

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
                 (file/directory-paths (str from-dir "/" bricks-dir "/" brick-name)))))

(defn src-dev-paths [from-dir bricks-dir brick-name]
  (source-paths from-dir bricks-dir brick-name 32 #{"src" "resources"} ""))

(defn test-dev-paths [from-dir bricks-dir brick-name]
  (source-paths from-dir bricks-dir brick-name 33 #{"test"} ""))

(defn src-env-paths [from-dir bricks-dir brick-name]
  (source-paths from-dir bricks-dir brick-name 11 #{"src" "resources"} "../../"))

(defn test-env-paths [from-dir bricks-dir brick-name]
  (source-paths from-dir bricks-dir brick-name 32 #{"test"} "../../"))


(defn lib-row [[lib version] n#spaces]
  (str (str-util/spaces n#spaces) lib " {:mvn/version\" " version "\"}"))

(defn dev-deps-content [from-dir top-ns component-names base-names libraries]
  (concat
    [(str "")
     (str "{:polylith {:vcs \"git\"")
     (str "            :top-namespace \"" top-ns "\"")
     (str "            :interface-ns \"interface\"")
     (str "            :env->alias {\"development\" \"dev\"}")
     (str "            :ns->lib {clojure             org.clojure/clojure")
     (str "                      clojure.tools.deps  org.clojure/tools.deps.alpha}}")
     (str "")
     (str " :aliases  {:dev {:extra-paths [; Development")
     (str "                                \"development/src\"")
     (str "                                ; Components")]
    (mapcat #(src-dev-paths from-dir "components" %) component-names)
    [(str "                                ; Bases")]
    (mapcat #(src-dev-paths from-dir "bases" %) base-names)
    [(str "                               ]")
     (str "                  :extra-deps {")]
    (mapv #(lib-row % 31) libraries)
    [(str "                              }}")
     (str "            :test {:extra-paths [; Components")]
    (mapcat #(test-dev-paths from-dir "components" %) component-names)
    [(str "                                 ; Bases")]
    (mapcat #(test-dev-paths from-dir "bases" %) base-names)
    [(str "                                 ]}")
     (str "")
     (str "            :poly {:main-opts [\"-m\" \"polylith.clj.core.poly_cli.poly\"]")
     (str "                   :extra-deps {tengstrand/polylith")
     (str "                                {:git/url   \"https://github.com/tengstrand/polylith.git\"")
     (str "                                 :sha       \"" common/poly-git-sha "\"")
     (str "                                 :deps/root \"environments/cli\"}}}}}")]))

(defn env-deps-content [from-dir component-names base-names libraries]
  (concat
    [(str "")
     (str "{:paths   [; Components")]
    (mapcat #(src-env-paths from-dir "components" %) component-names)
    [(str "           ; Bases")]
    (mapcat #(src-env-paths from-dir "bases" %) base-names)
    [(str "                               ]")
     (str " :deps    {")]
    (mapv #(lib-row % 11) libraries)
    [(str "          }")
     (str " :aliases {:test {:extra-paths [; Components")]
    (mapcat #(test-env-paths from-dir "components" %) component-names)
    [(str "")
     (str "                                 ; Bases")]
    (mapcat #(test-env-paths from-dir "bases" %) base-names)
    [(str "                                 ]}}}")]))

(defn create-dev [from-dir to-dir top-ns component-names base-names]
  (let [dev-brick-names (map common/path-to-ns (file/directory-paths (str from-dir "/environments/development/src/" (common/ns-to-path top-ns))))
        dev-component-names (sort (filter #(contains? (set component-names) %) dev-brick-names))
        dev-base-names (sort (filter #(contains? (set base-names) %) dev-brick-names))
        libs (sort-by #(-> % first str) (config-key :dependencies (str from-dir "/environments/development")))]
    (file/create-file (str to-dir "/deps.edn")
                      (dev-deps-content from-dir top-ns dev-component-names dev-base-names libs))))

(defn create-env [from-dir to-dir env-name top-ns component-names base-names]
  (let [env-brick-names (map common/path-to-ns (file/directory-paths (str from-dir "/systems/" env-name "/src/" (common/ns-to-path top-ns))))
        env-component-names (sort (filter #(contains? (set component-names) %) env-brick-names))
        env-base-names (sort (filter #(contains? (set base-names) %) env-brick-names))
        libs (sort-by #(-> % first str) (config-key :dependencies (str from-dir "/systems/" env-name)))]
    (file/create-dir (str to-dir "/environments/" env-name))
    (file/create-file (str to-dir "/environments/" env-name "/deps.edn")
                      (env-deps-content from-dir env-component-names env-base-names libs))))

(defn migrate [from-dir]
  (let [to-dir (next-ws-dir from-dir)
        top-ns (-> :polylith (config-key from-dir) :top-namespace)
        component-names (sort (file/directory-paths (str from-dir "/components")))
        base-names (sort (file/directory-paths (str from-dir "/bases")))
        system-names (file/directory-paths (str from-dir "/systems"))]
    (file/create-dir to-dir)
    (copy-dir from-dir to-dir "components")
    (copy-dir from-dir to-dir "bases")
    (file/create-dir (str to-dir "/development"))
    (file/create-dir (str to-dir "/environments"))
    (file/create-dir (str to-dir "/development/src"))
    (file/create-file (str to-dir "/development/src/.keep") [""])
    (create-dev from-dir to-dir top-ns component-names base-names)
    (doseq [system-name system-names]
      (create-env from-dir to-dir system-name top-ns component-names base-names))))
