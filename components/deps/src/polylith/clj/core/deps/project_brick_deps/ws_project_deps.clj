(ns polylith.clj.core.deps.project-brick-deps.ws-project-deps
  (:require [clojure.set :as set]
            [polylith.clj.core.deps.project-brick-deps.from-namespaces :as from-namespaces]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.project-brick-deps.shared :as shared]))

(defn extract-src-deps [brick {:keys [alias settings]}]
  (let [top-namespace (:top-namespace settings)
        suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
        [_ deps] (from-namespaces/extract-src-deps brick suffixed-top-ns)]
    (map #(str alias "/" %)
         deps)))

(defn extract-test-deps [brick {:keys [alias settings]} bricks]
  (let [top-namespace (:top-namespace settings)
        suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
        all-test-namespaces (shared/all-test-namespaces bricks suffixed-top-ns)
        src-brick-id->brick-ids (into {} (map #(from-namespaces/extract-src-deps % suffixed-top-ns) bricks))
        [_ deps] (from-namespaces/extract-test-deps brick suffixed-top-ns all-test-namespaces src-brick-id->brick-ids)]
    (map #(str alias "/" %)
         deps)))

(defn merge-src-deps [{:keys [direct missing-ifc-and-bases] :as dep}
                      brick-names-src
                      brick
                      workspaces]
  (let [direct-deps (set/intersection (set (mapcat #(extract-src-deps brick %) workspaces))
                                      brick-names-src)
        missing-deps (set/difference direct-deps brick-names-src)
        all-direct-deps (vec (concat direct direct-deps))
        all-missing-deps (vec (concat missing-ifc-and-bases missing-deps))]
    (cond-> dep
            (seq all-direct-deps) (assoc :direct all-direct-deps)
            (seq all-missing-deps) (assoc :missing-ifc-and-bases all-missing-deps))))

(defn merge-test-deps [{:keys [direct missing-ifc-and-bases] :as dep}
                       brick-names-test
                       brick
                       bricks
                       workspaces]
  (let [direct-deps (set/intersection (set (mapcat #(extract-test-deps brick % bricks) workspaces))
                                      brick-names-test)
        missing-deps (set/difference direct-deps brick-names-test)
        all-direct-deps (vec (concat direct direct-deps))
        all-missing-deps (vec (concat missing-ifc-and-bases missing-deps))]
    (cond-> dep
            (seq all-direct-deps) (assoc :direct all-direct-deps)
            (seq all-missing-deps) (assoc :missing-ifc-and-bases all-missing-deps))))

(defn merge-deps [[brick-name {:keys [src test]}]
                  brick-names-src
                  brick-names-test
                  name->brick
                  bricks
                  workspaces]
  (let [brick (name->brick brick-name)
        src-deps (merge-src-deps src brick-names-src brick workspaces)
        test-deps (merge-test-deps test brick-names-test brick bricks workspaces)]
    [brick-name
     {:src src-deps
      :test test-deps}]))

(defn merge-ws-deps [brick-deps
                     component-names-src-x
                     component-names-test-x
                     base-names-src-x
                     base-names-test-x
                     bricks
                     workspaces]
  (let [name->brick (into {} (map (juxt :name identity) bricks))
        brick-names-src (set (concat component-names-src-x base-names-src-x))
        brick-names-test (set (concat component-names-test-x base-names-test-x))]
    (map #(merge-deps %
                      brick-names-src
                      brick-names-test
                      name->brick
                      bricks
                      workspaces)
         brick-deps)))
