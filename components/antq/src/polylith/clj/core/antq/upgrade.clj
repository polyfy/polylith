(ns ^:no-doc polylith.clj.core.antq.upgrade
  (:require [antq.api :as antq]
            [clojure.set :as set]
            [polylith.clj.core.antq.latest :as latest]
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

(defn upgrade-dep-in-config [entity-name type filename lib lib->latest-version settings color-mode]
  (when-not (keep-lib-version? entity-name type lib settings)
    (let [[[_ version] latest-version] (first (filter #(= lib (-> % ffirst symbol))
                                                  lib->latest-version))
          ok? (get (antq/upgrade-deps! [{:file filename
                                         :dependency {:project :clojure
                                                      :type :java
                                                      :name (str lib)
                                                      :version version
                                                      :latest-version latest-version}}])
                   true)]
      (when ok?
        (println (str "  Updated " lib " to " latest-version " in " (color/entity type entity-name color-mode) "."))))))

(defn upgrade-deps-in-config [{:keys [name type] :as config} outdated-libs lib->latest-version ws-dir settings color-mode]
  (let [filename (str ws-dir "/" type "s/" name "/deps.edn")]
    (doseq [lib (outdated-libs-in-config config outdated-libs)]
      (upgrade-dep-in-config name type filename lib lib->latest-version settings color-mode))))

(defn upgrade-all-libs! [{:keys [ws-dir settings] :as workspace} color-mode]
  (let [lib->latest-version (latest/library->latest-version workspace)
        outdated-libs (set (mapv #(-> % first symbol)
                                 (keys lib->latest-version)))
        outdated-configs (filter #(outdated-libs-in-config % outdated-libs)
                                 (latest/configs workspace))]
    (doseq [outdated-config outdated-configs]
      (upgrade-deps-in-config outdated-config outdated-libs lib->latest-version ws-dir settings color-mode))))
