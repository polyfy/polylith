(ns polylith.test.core
  (:require [clojure.string :as str]
            [polylith.common.interface :as common]
            [polylith.workspace.interface :as ws])
  (:refer-clojure :exclude [test]))

(def test-runner-dep {'com.cognitect/test-runner {:git/url "https://github.com/cognitect-labs/test-runner.git"
                                                  :sha     "209b64504cb3bd3b99ecfec7937b358a879f55c1"}})

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

(defn run-tests [{:keys [ws-path] :as workspace} env-name]
  (when (str/blank? env-name)
    (throw (ex-info "Environment name is required for the test command." {})))
  (let [env-group (group env-name)
        config (->config workspace)
        libraries (ws/resolve-libs config env-group true test-runner-dep)
        paths (ws/src-paths config env-group true)
        _ (throw-exception-if-empty paths env-group)
        classpath (common/make-classpath libraries paths)
        expression (str "(require '[cognitect.test-runner :as test-runner]) (def extra-paths " paths ") (test-runner/test {:dir extra-paths})")
        out (common/run-in-jvm classpath expression ws-path "Could not run tests.")
        split-out (str/split-lines out)
        {:keys [error fail pass] :as summary} (-> split-out last read-string)]
    (println (str/join "\n" (drop-last 2 split-out)))
    (when (or (< 0 error)
              (< 0 fail))
      (throw (ex-info (str "Test results: " pass " passes, " fail " failures, " error " errors.") summary)))
    (println "\nTest results:" pass "passes," fail "failures," error "errors.")
    summary))
