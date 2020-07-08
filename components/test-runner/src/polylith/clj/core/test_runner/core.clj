(ns polylith.clj.core.test-runner.core
  (:require [clojure.tools.deps.alpha :as tools-deps]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color])
  (:refer-clojure :exclude [test]))

(defn key-as-symbol [[library version]]
  "The library names (keys) are stored as strings in the workspace
   and need to be converted back to symbols here."
  [(symbol library) version])

(defn ->config [workspace {:keys [deps test-deps maven-repos]}]
  (assoc workspace :mvn/repos maven-repos
                   :deps (into {} (map key-as-symbol (merge deps test-deps)))))

(defn ->test-statement [ns-name]
  (let [ns-symbol (symbol ns-name)]
    `(do (use 'clojure.test)
         (require '~ns-symbol)
         (clojure.test/run-tests '~ns-symbol))))

(defn resolve-deps [{:keys [deps] :as config}]
  (try
    (into #{} (mapcat #(-> % second :paths)
                      (tools-deps/resolve-deps config {:extra-deps deps})))
    (catch Exception e
      (println e)
      (throw e))))

(defn ->test-namespaces [bases components test-base-names test-component-names]
  (let [base-name->namespaces (into {} (map (juxt :name :namespaces-test) bases))
        component-name->namespaces (into {} (map (juxt :name :namespaces-test) components))
        base-namespaces (mapv :namespace (mapcat base-name->namespaces test-base-names))
        component-namespaces (mapv :namespace (mapcat component-name->namespaces test-component-names))]
    (concat base-namespaces component-namespaces)))

(defn ->root-path []
  (let [root-path (file/absolute-path ".")]
    (subs root-path 0 (dec (count root-path)))))

(defn full-path [root-path path]
  (str root-path (subs path 6)))

(defn run-tests-for-environment [{:keys [bases components] :as workspace}
                                 {:keys [test-base-names test-component-names paths test-paths] :as environment}]
  (when (-> test-paths empty? not)
    (let [color-mode (-> workspace :settings :color-mode)
          config (->config workspace environment)
          lib-paths (resolve-deps config)
          root-path (->root-path)
          src-paths (mapv #(full-path root-path %) (set (concat paths test-paths)))
          paths (concat src-paths lib-paths)
          test-namespaces (->test-namespaces bases components test-base-names test-component-names)
          test-statements (map ->test-statement test-namespaces)
          class-loader (common/create-class-loader paths color-mode)]
      (doseq [statement test-statements]
        (let [{:keys [error fail pass] :as summary}
              (try
                (common/eval-in class-loader statement)
                (catch Exception e
                  (.printStackTrace e)
                  (println (str (color/error color-mode "Couldn't run test statement: ") statement " " (color/error color-mode e)))))
              result-str (str "Test results: " pass " passes, " fail " failures, " error " errors.")]
          (when (or (nil? error)
                    (< 0 error)
                    (< 0 fail))
            (throw (Exception. (str "\n" (color/error color-mode result-str)) summary)))
          (println (str "\n" (color/ok color-mode result-str))))))))

(defn run-all-tests [workspace environments]
  (doseq [environment environments]
    (run-tests-for-environment workspace environment)))

(defn run [{:keys [environments] :as workspace} env]
  (if (nil? env)
    (run-all-tests workspace environments)
    (if-let [environment (util/find-first #(= env (:name %)) environments)]
      (run-tests-for-environment workspace environment)
      (println (str "Can't find environment '" env "'.")))))
