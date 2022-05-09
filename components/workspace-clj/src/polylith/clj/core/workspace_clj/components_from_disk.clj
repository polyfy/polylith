(ns polylith.clj.core.workspace-clj.components-from-disk
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace-clj.brick-paths :as brick-paths]
            [polylith.clj.core.workspace-clj.config-from-disk :as config-from-disk]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.workspace-clj.interface-defs-from-disk :as defs-from-disk]))


(defn make-brick-paths [brick-paths component-dir top-src-dir]
  (vec (map #(str component-dir "/" % "/" top-src-dir) brick-paths)))

(defn read-component [ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir component-name interface-ns brick->non-top-namespaces]
  (let [component-dir (str ws-dir "/components/" component-name)

        config (config-from-disk/read-config-file ws-type component-dir)
        brick-dirs (brick-paths/source-paths component-dir config)

        component-src-dirs (make-brick-paths (:src brick-dirs) component-dir top-src-dir) ; ["src"]
        component-src-dir (str component-dir "/src/" top-src-dir) ; /home/nkabir/labkey/bench/xpkg/rkx/unstable/rkpoly/vendor/polylith/components/common/src/polylith/clj/core/

        component-test-dirs (make-brick-paths (:test brick-dirs) component-dir top-src-dir) ; /home/nkabir/labkey/bench/xpkg/rkx/unstable/rkpoly/vendor/polylith/components/change/test/polylith/clj/core/
        component-test-dir (str component-dir "/test/" top-src-dir) ;

        interface-path-name (-> component-src-dir file/directories first)


        interface-name (common/path-to-ns interface-path-name)
        src-dir (str component-src-dir interface-path-name)
        namespaces (ns-from-disk/namespaces-from-disk component-src-dir component-test-dir)
        definitions (defs-from-disk/defs-from-disk src-dir interface-ns)
        entity-root-path (str "components/" component-name)
        lib-deps (lib/brick-lib-deps ws-dir ws-type config top-namespace ns-to-lib namespaces entity-root-path user-home)]

    (println "vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv")

    (println (str component-dir))
    (println (str top-src-dir))
    (println (str component-src-dir))

    (println (pr-str component-src-dirs))

    (println (str component-test-dir))
    (println (pr-str component-test-dirs))

    (println (str interface-path-name))
    (println "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^")
    (util/ordered-map :name component-name
                      :type "component"
                      :maven-repos (:mvn/repos config)
                      :paths (brick-paths/source-paths component-dir config)
                      :namespaces namespaces
                      :non-top-namespaces (brick->non-top-namespaces component-name)
                      :lib-deps lib-deps
                      :interface (util/ordered-map :name interface-name
                                                      :definitions definitions))))

(defn read-components [ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir interface-ns brick->non-top-namespaces]
  (vec (sort-by :name (map #(read-component ws-dir ws-type user-home top-namespace ns-to-lib top-src-dir % interface-ns brick->non-top-namespaces)
                           (file/directories (str ws-dir "/components"))))))
