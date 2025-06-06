(ns ^:no-doc polylith.clj.core.clojure-test-test-runner.core
  (:require [clojure.string :as str]
            [polylith.clj.core.test-runner-contract.interface :as test-runner-contract]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn- clj-namespace? [{:keys [file-path]}]
  (or (str/ends-with? file-path ".clj")
      (str/ends-with? file-path ".cljc")))

(defn brick-test-namespaces [bricks test-brick-names]
  (let [brick-name->namespaces (->> bricks
                                    (map (juxt :name #(-> % :namespaces :test)))
                                    (into {}))]
    (->> test-brick-names
         (mapcat brick-name->namespaces)
         (filter clj-namespace?)
         (map :namespace)
         (into []))))

(defn project-test-namespaces [project-name projects-to-test namespaces]
  (when (contains? (set projects-to-test) project-name)
    (->> (:test namespaces)
         (filter clj-namespace?)
         (map :namespace)
         (into []))))

(defn components-msg [component-names color-mode]
  (when (seq component-names)
    [(color/component (str/join ", " component-names) color-mode)]))

(defn bases-msg [base-names color-mode]
  (when (seq base-names)
    [(color/base (str/join ", " base-names) color-mode)]))

(defn run-message [project-name components bases bricks-to-test projects-to-test color-mode]
  (let [component-names (into #{} (map :name) components)
        base-names (into #{} (map :name) bases)
        bases-to-test (filterv #(contains? base-names %) bricks-to-test)
        bases-to-test-msg (bases-msg bases-to-test color-mode)
        components-to-test (filterv #(contains? component-names %) bricks-to-test)
        components-to-test-msg (components-msg components-to-test color-mode)
        projects-to-test-msg (when (seq projects-to-test)
                               [(color/project (str/join ", " projects-to-test) color-mode)])
        entities-msg (str/join ", " (into [] cat [components-to-test-msg
                                                  bases-to-test-msg
                                                  projects-to-test-msg]))
        project-cnt (count projects-to-test)
        bricks-cnt (count bricks-to-test)
        project-msg (if (zero? project-cnt)
                      ""
                      (str " and " (str-util/count-things "project" project-cnt)))]
    (str "Running tests from the " (color/project project-name color-mode) " project, including "
         (str-util/count-things "brick" bricks-cnt) project-msg ": " entities-msg)))

(defn run-test-statements [project-name eval-in-project ns-names run-message is-verbose color-mode]
  (println (str run-message))

  (doseq [ns-name ns-names]
    (let [ns-symbol (symbol ns-name)
          {:keys [error fail pass]}
          (try
            (when is-verbose (println "\n# namespace:" ns-symbol))
            (eval-in-project `(require '~ns-symbol))
            (eval-in-project `(use 'clojure.test))
            (eval-in-project `(clojure.test/run-tests '~ns-symbol))
            (catch Exception e
              (.printStackTrace e)
              (println (str (color/error color-mode "Couldn't run test statement") " for the " (color/project project-name color-mode) ", namespace: " ns-symbol ", " (color/error color-mode e)))))
          result-str (str "Test results: " pass " passes, " fail " failures, " error " errors.")]
      (when (or (nil? error)
                (< 0 error)
                (< 0 fail))
        (throw (Exception. (str "\n" (color/error color-mode result-str)))))
      (println (str "\n" (color/ok color-mode result-str))))))

(defn create
  "The arguments to this function form an implicit contract with any third
   party test runners. In particular, changes to this function need to be
   reflected in:
   * https://github.com/imrekoszo/polylith-kaocha
   * https://github.com/seancorfield/polylith-external-test-runner
     and any others that get built by the community."
  [{:keys [workspace project]}]
  (let [{:keys [bases components]} workspace
        {:keys [name bricks-to-test projects-to-test namespaces paths]} project

        ;; TODO: if the project tests aren't to be run, we might further narrow this down
        test-sources-present* (delay (-> paths :test seq))
        bricks-to-test* (delay bricks-to-test)
        projects-to-test* (delay projects-to-test)
        ns-names* (->> [(brick-test-namespaces (into components bases) @bricks-to-test*)
                        (project-test-namespaces name @projects-to-test* namespaces)]
                       (into [] cat)
                       (delay))]

    (reify test-runner-contract/TestRunner
      (test-runner-name [_] "Polylith built-in clojure.test runner")

      (test-sources-present? [_] @test-sources-present*)

      (tests-present? [this {_eval-in-project :eval-in-project :as _opts}]
        (and (test-runner-contract/test-sources-present? this)
             (seq @ns-names*)))

      (run-tests [this {:keys [color-mode eval-in-project is-verbose] :as opts}]
        (when (test-runner-contract/tests-present? this opts)
          (let [run-message (run-message name components bases @bricks-to-test*
                                         @projects-to-test* color-mode)]
            (run-test-statements
             name eval-in-project @ns-names* run-message is-verbose color-mode)))))))
