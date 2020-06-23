(ns polylith.workspace.dependencies
  (:require [clojure.tools.deps.alpha :as tools-deps]
            [polylith.workspace.environment :as env]))

(defn select-deps [{:keys [environments]} env-group include-tests? additional-deps]
  (let [envs (env/select environments env-group include-tests?)]
    (merge additional-deps
           (into (sorted-map) (mapcat :deps envs)))))

(defn paths [workspace env-group include-tests? additional-deps]
  (let [deps (select-deps workspace env-group include-tests? additional-deps)
        resolved-deps (tools-deps/resolve-deps workspace {:extra-deps deps})]
    (into #{} (mapcat #(-> % second :paths) resolved-deps))))
