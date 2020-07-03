(ns polylith.core.workspace.dependencies
  (:require [clojure.tools.deps.alpha :as tools-deps]
            [polylith.core.workspace.environment :as env]))

(defn select-deps [{:keys [environments]} env-group include-tests? additional-deps]
  (let [envs (env/select environments env-group include-tests?)]
    (merge additional-deps
           (into (sorted-map) (mapcat :deps envs)))))

(defn resolve-deps [workspace deps]
  (try
    (tools-deps/resolve-deps workspace {:extra-deps deps})
    (catch Exception e
      (println e)
      (throw e))))

(defn paths [workspace env-group include-tests? additional-deps]
  (let [deps (select-deps workspace env-group include-tests? additional-deps)
        resolved-deps (resolve-deps workspace deps)]
    (into #{} (mapcat #(-> % second :paths) resolved-deps))))
