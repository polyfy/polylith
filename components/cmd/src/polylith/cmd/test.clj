(ns polylith.cmd.test
  (:require [clojure.string :as str])
            ;[polylith.common.interface :as common])
  (:refer-clojure :exclude [test]))

;(def test-runner-dep {'com.cognitect/test-runner {:git/url "https://github.com/cognitect-labs/test-runner.git"
;                                                  :sha     "209b64504cb3bd3b99ecfec7937b358a879f55c1"}})
;
;(defn test [ws-path deps service-or-env]
;  (when (str/blank? service-or-env)
;    (throw (ex-info "Service or environment name is required for test command." {})))
;  (let [libraries  (common/resolve-libraries deps service-or-env true test-runner-dep)
;        paths      (common/extract-source-paths ws-path deps service-or-env true)
;        _          (when (empty? paths)
;                     (throw (ex-info (str "No source paths found. Check service or environment name: " service-or-env)
;                                     {:service-or-env service-or-env})))
;        classpath  (common/make-classpath libraries paths)
;        expression (str "(require '[cognitect.test-runner :as test-runner]) (def extra-paths " paths ") (test-runner/test {:dir extra-paths})")
;        out        (common/run-in-jvm classpath expression ws-path "Could not run tests.")
;        split-out  (str/split-lines out)
;        {:keys [error fail pass] :as summary} (-> split-out last read-string)]
;    (println (str/join "\n" (drop-last 2 split-out)))
;    (when (or (< 0 error)
;              (< 0 fail))
;      (throw (ex-info (str "Test results: " pass " passes, " fail " failures, " error " errors.") summary)))
;    (println "\nTest results:" pass "passes," fail "failures," error "errors.")
;    summary))
