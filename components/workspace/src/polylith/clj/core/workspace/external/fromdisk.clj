(ns ^:no-doc polylith.clj.core.workspace.external.fromdisk
  (:require [clojure.string :as str]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.workspace.fromdisk.core :as fromdisk]
            [polylith.clj.core.workspace.enrich.core :as enrich]))

(defn ws-name [{:keys [dir]}]
  (when dir
    (last (str/split dir #"/"))))

(defn include-ws? [config ws-paths]
  (let [name (ws-name config)]
    (and name
         (not (contains? ws-paths name)))))

(declare workspace!)

(defn used-workspaces! [ws-dir workspace wsdir->workspace]
  (let [wss-config (-> workspace :configs :workspace :workspaces)
        ws-paths (set (keys @wsdir->workspace))
        configs (filter #(include-ws? % ws-paths)
                        wss-config)]
    (doseq [{:keys [dir alias]} configs]
      (let [path (file/absolute-path (str ws-dir "/" dir))
            workspace (workspace! wsdir->workspace)
            ws-alias (or alias (:name workspace))]
        (swap! wsdir->workspace #(assoc % path
                                          (assoc workspace :alias ws-alias)))))))

(defn workspace! [wsdir->workspace]
  (let [{:keys [config-errors ws-dir] :as workspace}
        (-> {}
            (fromdisk/workspace-from-disk)
            (enrich/enrich-workspace [])
            (change/with-changes))]

    (if (or (nil? workspace)
            (seq config-errors))
      workspace
      (do
        (used-workspaces! ws-dir workspace wsdir->workspace)
        workspace))))

(defn workspaces
  "Returns a vector with included workspaces, in the order they were referenced."
  [ws-dir]
  (let [path (file/absolute-path ws-dir)
        wsdir->workspace (atom {path (array-map)})
        _ (workspace! wsdir->workspace)]
    (mapv second
          (filter #(-> % second seq)
                  @wsdir->workspace))))
