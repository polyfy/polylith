(ns polylith.clj.core.command.deps
  (:require [polylith.clj.core.deps.interfc :as deps]
            [polylith.clj.core.common.interfc :as common]))

(defn deps [workspace environment-name brick-name unnamed-args show-lib?]
  (let [{:keys [ok? message]} (common/validate-args unnamed-args "deps env:my-env brick:my-brick")]
    (if ok?
      (if show-lib?
        (deps/print-lib-table workspace)
        (if environment-name
          (if brick-name
            (deps/print-brick-table workspace environment-name brick-name)
            (deps/print-workspace-brick-table workspace environment-name))
          (if brick-name
            (deps/print-brick-ifc-table workspace brick-name)
            (deps/print-workspace-ifc-table workspace))))
      (println message))))
