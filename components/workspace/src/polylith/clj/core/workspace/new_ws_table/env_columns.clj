(ns polylith.clj.core.workspace.new-ws-table.env-columns
  (:require [polylith.clj.core.workspace.new-ws-table.shared :as shared]
            [polylith.clj.core.util.interfc.color :as color]))

(defn env-sorting [{:keys [dev? name]}]
  [dev? name])

(defn alias-changes [[env changes] env->alias]
  [(env->alias env) (set changes)])

(defn env-brick-names [{:keys [alias
                               component-names base-names
                               test-component-names test-base-names]}]
  [alias {:src (set (concat component-names base-names))
          :test (set (concat test-component-names test-base-names))}])

(defn flag [dev? brick bricks value color-mode]
  (if (and dev? (contains? bricks brick))
    (color/cyan color-mode value)
    (color/purple color-mode value)))

(defn status-flags [alias dev? brick alias->bricks alias->bricks-to-test {:keys [src-bricks test-bricks]} color-mode]
  (let [has-src (if (contains? (-> alias alias->bricks :src) brick) "x" "-")
        has-test-src (if (contains? (-> alias alias->bricks :test) brick) "x" "-")
        to-test (if (contains? (alias->bricks-to-test alias) brick) "x" "-")]
    (str (flag dev? brick src-bricks has-src color-mode)
         (flag dev? brick test-bricks has-test-src color-mode)
         (color/purple color-mode to-test))))

(defn env-cell [index column {:keys [name]} alias dev? alias->bricks alias->bricks-to-test profile-bricks color-mode]
  (let [status (status-flags alias dev? name alias->bricks alias->bricks-to-test profile-bricks color-mode)
        color (if (and dev? (contains? profile-bricks name)) :cyan :purple)]
    (shared/standard-cell status column (+ index 3) color :center)))

(defn column [index {:keys [alias dev? total-lines-of-code-src]} bricks alias->bricks alias->bricks-to-test profile-bricks show-loc? thousand-sep color-mode]
  (let [column (+ 5 (* 2 index))]
    (concat
      [(shared/standard-cell alias column 1 :purple :center)]
      (map-indexed #(env-cell %1 column %2 alias dev? alias->bricks alias->bricks-to-test profile-bricks color-mode) bricks)
      (when show-loc? [(shared/number-cell total-lines-of-code-src column (+ 3 (count bricks)) :center thousand-sep)]))))

(defn columns [environments bricks {:keys [env->bricks-to-test]}
               {:keys [profile->settings active-dev-profiles]}
               show-loc? thousand-sep color-mode]
  (let [envs (sort-by env-sorting environments)
        env->alias (into {} (map (juxt :name :alias) envs))
        alias->bricks (into {} (map env-brick-names envs))
        alias->bricks-to-test (into {} (map #(alias-changes % env->alias) env->bricks-to-test))
        profile-bricks {:src-bricks (set (mapcat #(-> % profile->settings :src-bricks) active-dev-profiles))
                        :test-bricks (set (mapcat #(-> % profile->settings :test-bricks) active-dev-profiles))}]
    (apply concat
           (map-indexed #(column %1 %2 bricks alias->bricks alias->bricks-to-test profile-bricks show-loc? thousand-sep color-mode)
                        envs))))
