(ns polylith.clj.core.command.dependencies
  (:require [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.common.interface :as common]))

(defn deps [workspace project-name brick-name unnamed-args is-all]
  (let [{:keys [ok? message]} (common/validate-args unnamed-args "deps project:my-project brick:my-brick")]
    (if ok?
      (if project-name
        (if brick-name
          (deps/print-project-brick-table workspace project-name brick-name)
          (deps/print-project-table workspace project-name is-all))
        (if brick-name
          (deps/print-brick-table workspace brick-name)
          (deps/print-workspace-table workspace)))
      (println message))))
