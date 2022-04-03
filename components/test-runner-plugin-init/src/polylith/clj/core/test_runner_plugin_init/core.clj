(ns polylith.clj.core.test-runner-plugin-init.core
  (:require [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.test-runner-plugin.interface :as test-runner-plugin]))

(defn ->constructor-var [make-test-runner-sym]
  (when-not (contains? #{nil :default} make-test-runner-sym)
    (requiring-resolve make-test-runner-sym)))

(let [ampersand-symbol? #{'&}
      fully-variadic? #(ampersand-symbol? (first %))
      not-ampersand (complement ampersand-symbol?)
      has-one-fixed-arg #(= 1 (count (take-while not-ampersand %)))
      can-be-invoked-with-one-arg-xf (map (some-fn fully-variadic? has-one-fixed-arg))]

  (defn valid-constructor-var? [candidate]
    (and (fn? (deref candidate))
         (->> candidate (meta) (:arglists) (util/xf-some can-be-invoked-with-one-arg-xf)))))

(defn ensure-valid-test-runner [candidate]
  (if (satisfies? test-runner-plugin/TestRunner candidate)
    candidate
    (throw (ex-info "test runner must satisfy the TestRunner protocol" {}))))
