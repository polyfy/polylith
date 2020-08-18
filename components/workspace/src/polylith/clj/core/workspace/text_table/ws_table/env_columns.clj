(ns polylith.clj.core.workspace.text-table.ws-table.env-columns
  (:require [polylith.clj.core.path-finder.interfc :as path-finder]
            [polylith.clj.core.workspace.text-table.shared :as shared]))

(defn env-sorting [{:keys [dev? name]}]
  [dev? name])

(defn alias-changes [[env changes] env->alias]
  [(env->alias env) (set changes)])

(defn status-flags [alias brick alias->bricks-to-test path-entries show-resources?]
  (str (path-finder/brick-status-flags path-entries brick show-resources?)
       (if (contains? (alias->bricks-to-test alias) brick) "x" "-")))

(defn env-cell [index column {:keys [name]} alias alias->bricks-to-test path-entries show-resources?]
  (let [statuses (status-flags alias name alias->bricks-to-test path-entries show-resources?)]
    (shared/standard-cell statuses column (+ index 3) :purple :center)))

(defn column [index {:keys [alias dev? src-paths test-paths]}
              ws-dir settings bricks alias->bricks-to-test show-loc? show-resources? thousand-sep]
  (let [column (+ 5 (* 2 index))
        path-entries (path-finder/path-entries ws-dir dev? src-paths test-paths settings)
        bricks-in-env (set (path-finder/src-brick-names path-entries))
        total-loc-src (apply + (filter identity (map :lines-of-code-src (filter #(contains? bricks-in-env (:name %)) bricks))))]
    (concat
      [(shared/standard-cell alias column 1 :purple :center)]
      (map-indexed #(env-cell %1 column %2 alias alias->bricks-to-test path-entries show-resources?) bricks)
      (when show-loc? [(shared/number-cell total-loc-src column (+ 3 (count bricks)) :center thousand-sep)]))))

(defn columns [ws-dir settings environments bricks {:keys [env->bricks-to-test]}
               show-loc? show-resources? thousand-sep]
  (let [envs (sort-by env-sorting environments)
        env->alias (into {} (map (juxt :name :alias) envs))
        alias->bricks-to-test (into {} (map #(alias-changes % env->alias) env->bricks-to-test))]
    (apply concat
           (map-indexed #(column %1 %2 ws-dir settings bricks alias->bricks-to-test show-loc? show-resources? thousand-sep)
                        envs))))
