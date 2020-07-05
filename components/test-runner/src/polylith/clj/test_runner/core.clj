(ns polylith.clj.test-runner.core
  (:require [clojure.string :as str]
            [polylith.core.common.interfc :as common]
            [polylith.core.workspace.interfc :as ws]
            [polylith.core.util.interfc :as util]
            [polylith.core.util.interfc.color :as color])
  (:refer-clojure :exclude [test]))

(defn throw-exception-if-empty [paths env]
  (when (empty? paths)
    (throw (ex-info (str "No source paths found for environment '" env "'.")
                    {:env env}))))

(defn key-as-symbol [[library version]]
  [(symbol library) version])

(defn ->environment [{:keys [deps maven-repos] :as environment}]
  "The library names (keys) are stored as strings in the workspace
   and need to be converted to symbols here."
  (assoc environment :deps (into {} (map key-as-symbol deps))
                     :mvn/repos maven-repos))

(defn test-env? [{:keys [group test?]} env-group]
  (and test?
       (= group env-group)))

(defn ->config [{:keys [environments] :as workspace} env-group]
  ;; Get maven repo from the environment.
  (let [{:keys [maven-repos]} (util/find-first #(test-env? % env-group) environments)]
    (assoc workspace :mvn/repos maven-repos
                     :environments (mapv ->environment environments))))

(defn group [env]
  (if (str/ends-with? env "-test")
    (subs env 0 (- (count env) 5))
    env))

(defn ns-name->test-statement [ns-name]
  (let [ns-symbol (symbol ns-name)]
    `(do (use 'clojure.test)
         (require '~ns-symbol)
         (clojure.test/run-tests '~ns-symbol))))

(defn run-tests-for-environment [workspace env]
  (let [env-group (group env)
        color-mode (-> workspace :settings :color-mode)
        config (->config workspace env-group)
        lib-paths (ws/lib-paths config env-group true)
        src-paths (ws/src-paths config env-group true)
        _ (throw-exception-if-empty src-paths env-group)
        paths (concat src-paths lib-paths)
        test-namespaces (ws/test-namespaces config env-group)
        test-statements (map ns-name->test-statement test-namespaces)
        class-loader (common/create-class-loader paths color-mode)]
    (doseq [statement test-statements]
      (let [{:keys [error fail pass] :as summary} (try
                                                    (common/eval-in class-loader statement)
                                                    (catch Exception e
                                                      (println (str (color/error color-mode "Couldn't run test statement: ") statement " " (color/error color-mode e)))))
            result-str (str "Test results: " pass " passes, " fail " failures, " error " errors.")]
        (when (or (< 0 error)
                  (< 0 fail))
          (throw (ex-info (str "\n" (color/error color-mode result-str)) summary)))
        (println (str "\n" (color/ok color-mode result-str)))))))

(defn run-all-tests [{:keys [environments] :as workspace}]
  (doseq [{:keys [name]} (filter :test? environments)]
    (run-tests-for-environment workspace name)))

(defn run [workspace env]
  (if (nil? env)
    (run-all-tests workspace)
    (run-tests-for-environment workspace env)))
