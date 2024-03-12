(ns polylith.clj.core.workspace.external.fromdisk
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.workspace.fromdisk.core :as fromdisk]
            [polylith.clj.core.ws-file.interface :as ws-file]))

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
            workspace (workspace! {:ws-dir path} wsdir->workspace)
            ws-alias (or alias (:name workspace))]
        (swap! wsdir->workspace #(assoc % path
                                          (assoc workspace :alias ws-alias)))))))

(defn workspace! [{:keys [ws-file] :as user-input} wsdir->workspace]
  (if ws-file
    (ws-file/read-ws-from-file ws-file user-input)
    (let [{:keys [config-errors ws-dir] :as workspace} (fromdisk/workspace-from-disk user-input)]
      (if (or (nil? workspace)
              (seq config-errors))
        workspace
        (do
          (used-workspaces! ws-dir workspace wsdir->workspace)
          workspace)))))

(defn workspaces
  "Returns a vector with included workspaces"
  [ws-dir user-input]
  (let [path (file/absolute-path ws-dir)
        wsdir->workspace (atom {path {}})
        _ (workspace! user-input wsdir->workspace)]
    (mapv second
          (filter #(-> % second seq)
                  @wsdir->workspace))))
