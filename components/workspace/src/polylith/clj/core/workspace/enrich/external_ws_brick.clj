(ns ^:no-doc polylith.clj.core.workspace.enrich.external-ws-brick
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.path :as util-path]))

(defn brick [{:keys [alias interface name type]}]
  {:brick (cond-> {:alias alias
                   :type type
                   :name name}
                  interface (assoc :interface interface))})

(defn interface-name [{:keys [components]} component-name]
  (-> (util/find-first #(= component-name (:name %)) components)
      :interface :name))

(defn brick-lib [[lib-key {:keys [path]}]
                 dir->alias
                 alias->workspace]
  (when path
    (let [[suffixed-path alias] (first (filter #(str/starts-with? path (first %))
                                               dir->alias))
          brick-path (subs path (count suffixed-path))
          [type brick-name] (cond (str/starts-with? brick-path "bases/") [:base (subs brick-path 6)]
                                  (str/starts-with? brick-path "components/") [:component (subs brick-path 11)])
          workspace (alias->workspace alias)
          interface (interface-name workspace brick-name)]
      (when (and alias type)
        {:lib-key lib-key
         :alias alias
         :type type
         :interface interface
         :name brick-name
         :path path}))))

(defn full-name [{:keys [alias name]}]
  (str alias "/" name))

(defn convert-libs-to-bricks
  "When we read all workspaces from disk, we treat all :local/root as libraries.
   This function adds the :brick key to libraries that refer bricks in other workspaces."
  [lib-deps ws-dir workspaces]
  (let [alias->workspace (into {} (map (juxt :alias identity) workspaces))
        dir->alias (into {} (map (juxt #(str (util-path/relative-path (file/absolute-path ws-dir)
                                                                      (-> % :ws-dir file/absolute-path)) "/") :alias)
                                 workspaces))
        brick-libs (filter identity
                           (map #(brick-lib % dir->alias alias->workspace)
                                lib-deps))
        base-names (vec (sort (mapv full-name
                                    (filter #(= :base (:type %))
                                            brick-libs))))
        component-names (vec (sort (mapv full-name
                                         (filter #(= :component (:type %))
                                                 brick-libs))))
        ws-bricks (into {} (map (juxt :lib-key brick)
                                brick-libs))
        lib-deps-with-ws-bricks (merge-with merge lib-deps ws-bricks)]
    [base-names
     component-names
     lib-deps-with-ws-bricks]))
