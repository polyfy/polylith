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
                  {:service-or-env env}))))

(defn test [{:keys [ws-path] :as workspace} env]
  (when (str/blank? env)
    (throw (ex-info "Environment name is required for the test command." {})))
  (let [libraries (ws/resolve-libs workspace env true test-runner-dep)
        paths (ws/src-paths workspace env true)
        _ (throw-exception-if-empty paths env)
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
