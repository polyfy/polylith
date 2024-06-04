(ns ^:no-doc polylith.clj.core.workspace.external.fromdisk
  (:require [clojure.string :as str]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface.path :as path-util]
            [polylith.clj.core.workspace.enrich.core :as enrich]
            [polylith.clj.core.workspace.fromdisk.core :as fromdisk]))

(defn ws-name [{:keys [dir]}]
  (when dir
    (last (str/split dir #"/"))))

(defn include-ws? [config ws-paths]
  (let [name (ws-name config)]
    (and name
         (not (contains? ws-paths name)))))

(declare workspace!)

(defn used-workspaces! [root-ws-dir ws-dir workspace wsdir-workspace]
  (let [wss-config (-> workspace :configs :workspace :workspaces)
        no-changes? (-> workspace :user-input :is-no-changes)
        ws-paths (set (map first @wsdir-workspace))
        configs (filter #(include-ws? % ws-paths)
                        wss-config)]
    (doseq [{:keys [dir alias]} configs]
      (let [path (file/absolute-path (str ws-dir "/" dir))
            workspace (workspace! root-ws-dir {:ws-dir path :is-no-changes no-changes?} wsdir-workspace)
            ws-alias (or alias (:name workspace))]
        (swap! wsdir-workspace #(conj % [path
                                         (assoc workspace :alias ws-alias)]))))))

(defn workspace! [root-ws-dir user-input wsdir-workspace]
  (let [{:keys [config-errors ws-dir] :as workspace}
        (fromdisk/workspace-from-disk user-input)
        relative-dir (path-util/relative-path (file/absolute-path root-ws-dir) (file/absolute-path ws-dir))]
    (if (or (nil? workspace)
            (seq config-errors))
      workspace
      (do
        (used-workspaces! root-ws-dir ws-dir workspace wsdir-workspace)
        (assoc workspace :ws-relative-dir relative-dir)))))

(defn workspaces
  "Returns a vector with included workspaces, in the order they were referenced."
  [{:keys [ws-dir] :as workspace}]
  (let [path (file/absolute-path ws-dir)
        wsdir-workspace (atom [[path]])
        _ (used-workspaces! ws-dir ws-dir workspace wsdir-workspace)
        workspaces (mapv #(-> % second atom)
                         (filter #(-> % second seq)
                                 @wsdir-workspace))]
    ;; Make sure we continuously update the workspaces, so that we can calculate BRICK:interface-deps correctly.
    (doseq [ws-atom workspaces]
      (reset! ws-atom (-> @ws-atom
                          (enrich/enrich-workspace (map deref workspaces))
                          (change/with-changes))))
    (vec (reverse (map deref workspaces)))))
