(ns polylith.clj.core.deps.project-brick-deps.shared
  (:require [polylith.clj.core.common.interface :as common]))

(defn ->brick-id [{:keys [name interface]}]
  (or (:name interface) name))

(defn test-namespace [{:keys [namespace]} suffixed-top-ns]
  (let [{:keys [root-ns depends-on-ns]} (common/extract-namespace suffixed-top-ns namespace)]
    (when root-ns
      [(str root-ns "." depends-on-ns)])))

(defn test-namespaces [{:keys [namespaces]} suffixed-top-ns]
  (mapcat #(test-namespace % suffixed-top-ns) (:test namespaces)))

(defn all-test-namespaces
  "Extracts all test namespaces from alla bricks in the project."
  [bricks suffixed-top-ns]
  (set (mapcat #(test-namespaces % suffixed-top-ns) bricks)))
