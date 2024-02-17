(ns ^:no-doc polylith.clj.core.workspace.fromdisk.project-paths
  (:require [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.select :as select]))

(defn source-paths [project-name is-dev src-paths]
  (if is-dev
    src-paths
    (mapv #(str "projects/" project-name "/" %)
          src-paths)))

(defn source-dirs [ws-dir project-name entries src-path-criteria]
  (mapv #(str ws-dir "/" %)
        (select/paths entries c/project? src-path-criteria (c/=name project-name))))

(defn project-source-dirs [ws-dir project-name is-dev project-src-paths project-test-paths]
  (let [entries (extract/from-paths {:src (source-paths project-name is-dev project-src-paths)
                                     :test (source-paths project-name is-dev project-test-paths)}
                                    {})]
    {:src-dirs (source-dirs ws-dir project-name entries c/src-path?)
     :test-dirs (source-dirs ws-dir project-name entries c/test-path?)}))
