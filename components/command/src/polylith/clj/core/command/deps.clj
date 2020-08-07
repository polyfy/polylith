(ns polylith.clj.core.command.deps
  (:require [polylith.clj.core.deps.interfc :as deps]))

(defn validate [unnamed-args]
  (if (-> unnamed-args empty? not)
    {:message "  Arguments should be passed by name, e.g.: deps env:my-env brick:my-brick"}
    {:ok? true}))

(defn deps [workspace environment-name brick-name unnamed-args color-mode]
  (let [{:keys [ok? message]} (validate unnamed-args)]
    (if ok?
      (if environment-name
        (if brick-name
          (deps/print-brick-table workspace environment-name brick-name color-mode)
          (deps/print-workspace-brick-table workspace environment-name color-mode))
        (if brick-name
          (deps/print-brick-ifc-table workspace brick-name color-mode)
          (deps/print-workspace-ifc-table workspace color-mode)))
      (println message))))
