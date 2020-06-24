(ns polylith.test.core
  (:require [clojure.string :as str]
            [polylith.common.interface :as common]
            [polylith.workspace.interface :as ws]
            [polylith.common.interface.color :as color])
  (:refer-clojure :exclude [test]))

(defn throw-exception-if-empty [paths env]
  (when (empty? paths)
    (throw (ex-info (str "No source paths found for environment '" env "'.")
                    {:env env}))))

(defn key-as-symbol [[library version]]
  [(symbol library) version])

(defn ->environment [{:keys [deps] :as environment}]
  "The library names (keys) are stored as strings in the workspace
   and need to be converted to symbols here."
  (assoc environment :deps
                     (into {} (map key-as-symbol deps))))

(defn ->config [{:keys [settings environments] :as workspace}]
  (assoc workspace :mvn/repos (:maven-repos settings)
                   :environments (mapv ->environment environments)))

(defn group [env]
  (if (str/ends-with? env "-test")
    (subs env 0 (- (count env) 5))
    env))

(defn ns-name->test-statement [ns-name]
  (let [ns-symbol (symbol ns-name)]
    `(do (use 'clojure.test)
         (require '~ns-symbol)
         (clojure.test/run-tests '~ns-symbol))))

(defn run-tests [workspace env-name]
  (when (str/blank? env-name)
    (throw (ex-info "Environment name is required for the test command." {})))
  (let [env-group (group env-name)
        config (->config workspace)
        lib-paths (ws/lib-paths config env-group true)
        src-paths (ws/src-paths config env-group true)
        _ (throw-exception-if-empty src-paths env-group)
        paths (concat src-paths lib-paths)
        test-namespaces (ws/test-namespaces config env-group)
        test-statements (map ns-name->test-statement test-namespaces)
        class-loader (common/create-class-loader paths)]
    (doseq [statement test-statements]
      (let [{:keys [error fail pass] :as summary} (common/eval-in class-loader statement)
            result-str (str "Test results: " pass " passes, " fail " failures, " error " errors.")]
        (when (or (< 0 error)
                  (< 0 fail))
          (throw (ex-info (str "\n" (color/as-red result-str)) summary)))
        (println (str "\n" (color/as-green result-str)))))))
