(ns polylith.clj.core.workspace.settings-enricher
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interfc.entity :as entity]))

(defn brick-path? [path]
  (or (str/starts-with? path "components/")
      (str/starts-with? path "bases/")))

(defn src-path? [path]
  (and (brick-path? path)
       (not (str/ends-with? path "/test"))))

(defn test-path? [path]
  (and (brick-path? path)
       (str/ends-with? path "/test")))

(defn with-bricks [[profile {:keys [paths deps]}]]
  (let [src-paths (vec (sort (filter src-path? paths)))
        test-paths (vec (sort (filter test-path? paths)))]
    [profile
     {:deps (or deps {})
      :src-bricks (set (entity/bricks-from-paths src-paths))
      :test-bricks (set (entity/bricks-from-paths test-paths))
      :src-paths src-paths
      :test-paths test-paths}]))

(defn enrich [{:keys [profile->settings] :as settings}
              {:keys [active-dev-profiles]}]
  (assoc settings :active-dev-profiles active-dev-profiles
                  :profile->settings (into {} (map with-bricks profile->settings))))
