(ns ^:no-doc polylith.clj.core.antq.upgrade
  (:require [antq.api :as antq]
            [clojure.set :as set]
            [polylith.clj.core.antq.outdated :as latest]
            [polylith.clj.core.util.interface.color :as color]))

(defn outdated-libs-in-config [config outdated-libs]
  (seq (set/intersection outdated-libs
                         (-> config :deps :deps keys set))))

(defn keep? [entity-name lib entity-config]
  (contains? (set (get-in entity-config [entity-name :keep-lib-versions]))
             lib))

(defn keep-lib-version? [entity-name type lib {:keys [bricks projects]}]
  (if (= "project" type)
    (keep? entity-name lib projects)
    (keep? entity-name lib bricks)))

(defn upgrade-lib [filename lib version latest-version]
  (get (antq/upgrade-deps! [{:file filename
                             :dependency {:project :clojure
                                          :type :java
                                          :name (str lib)
                                          :version version
                                          :latest-version latest-version}}])
       true))

(defn upgrade-dep-in-config [entity-name type filename lib lib->latest-version settings type->name->lib->version color-mode]
  (when-not (keep-lib-version? entity-name type lib settings)
    (let [[[lib-name version] latest-version] (first (filter #(= lib (-> % ffirst symbol))
                                                      lib->latest-version))
          updated? (and (= version
                           (get-in type->name->lib->version [(keyword type) entity-name (symbol lib-name)]))
                        (upgrade-lib filename lib version latest-version))]
      (if updated?
        (println (str "  Updated " lib " to " latest-version " in " (color/entity type entity-name color-mode) "."))))))

(defn upgrade-deps-in-config [{:keys [name type] :as config} outdated-libs lib->latest-version ws-dir settings type->name->lib->version color-mode]
  (let [filename (str ws-dir "/" type "s/" name "/deps.edn")
        config-libs (set (map symbol (-> config :deps :deps keys)))]
    (doseq [lib (outdated-libs-in-config config outdated-libs)]
      (when (contains? config-libs lib)
        (upgrade-dep-in-config name type filename lib lib->latest-version settings type->name->lib->version color-mode)))))

(defn upgrade-libs! [{:keys [ws-dir configs settings]} libraries-to-update type->name->lib->version color-mode]
  (let [lib->latest-version (latest/library->latest-version configs)
        outdated-libs (set (map #(-> % first symbol)
                                (keys lib->latest-version)))
        libs-to-update (if (empty? libraries-to-update)
                         outdated-libs
                         (set/intersection outdated-libs
                                           (set (map symbol libraries-to-update))))
        outdated-configs (filter #(outdated-libs-in-config % outdated-libs)
                                 (latest/entity-configs configs))]
    (doseq [outdated-config outdated-configs]
      (upgrade-deps-in-config outdated-config libs-to-update lib->latest-version ws-dir settings type->name->lib->version color-mode))))
