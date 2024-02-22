(ns polylith.clj.core.workspace.core
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.workspace.enrich.core :as enrich]
            [polylith.clj.core.change.interface :as change]
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
        ws-dirs (map :dir
                     (filter #(include-ws? % ws-paths)
                             wss-config))]
    (doseq [dir ws-dirs]
      (let [path (file/absolute-path (str ws-dir "/" dir))
            workspace (workspace! {:ws-dir path} wsdir->workspace)]
        (swap! wsdir->workspace #(assoc % path workspace))))))

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

(defn workspace [{:keys [ws-file] :as user-input}]
  (if ws-file
    (ws-file/read-ws-from-file ws-file user-input)
    (let [{:keys [config-errors ws-dir] :as workspace} (fromdisk/workspace-from-disk user-input)]
      (if (or (nil? workspace)
              (seq config-errors))
        workspace
        (let [path (file/absolute-path ws-dir)
              wsdir->workspace (atom {path {}})
              _ (workspace! user-input wsdir->workspace)
              workspaces (mapv second
                               (filter #(-> % second seq)
                                       @wsdir->workspace))]
          (cond->  workspace
                   (seq workspaces) (assoc :workspaces workspaces)
                   true enrich/enrich-workspace
                   true change/with-changes))))))

(comment
  (require '[polylith.clj.core.user-input.interface :as user-input])
  (def input (user-input/extract-arguments ["info" "ws-dir:examples/multiple-workspaces2/backend"]))

  (:workspaces (workspace input))
  #__)
