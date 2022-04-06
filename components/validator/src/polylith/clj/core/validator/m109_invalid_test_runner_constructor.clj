(ns polylith.clj.core.validator.m109-invalid-test-runner-constructor
  (:require
   [clojure.string :as str]
   [polylith.clj.core.test-runner-contract.interface.initializers :as test-runner-initializers]
   [polylith.clj.core.test-runner-contract.interface.verifiers :as test-runner-verifiers]
   [polylith.clj.core.util.interface :as util]
   [polylith.clj.core.util.interface.color :as color]))

(defn invalid-constructor? [candidate]
  (and (some? candidate)
       (not (test-runner-verifiers/valid-constructor-var? candidate))))

(defn error-or-maybe-constructor-var [candidate ->error-message]
  (try {:constructor-var (test-runner-initializers/->constructor-var candidate)}
       (catch Exception e
         {:error (->error-message (str "Unable to load test runner constructor " candidate) e)})))

(defn invalid-constructor-message [candidate ->error-message]
  (let [{:keys [constructor-var error]} (error-or-maybe-constructor-var candidate ->error-message)]
    (or error
        (when (invalid-constructor? constructor-var)
          (->error-message (str "The var referred to by " candidate " is not a valid test runner constructor"))))))

(defn multiple? [coll]
  (util/xf-some (comp (drop 1) (map any?)) coll))

(defn error-message-fn
  [color-mode project-names]
  (fn error-message
    ([text]
     (error-message text nil))
    ([text ex]
     (let [project-s (if (multiple? project-names) "projects" "project")]
       (str "Invalid test runner configuration for " project-s " "
            (str/join ", " (mapv #(color/project % color-mode) project-names))
            ". " text "."
            (when ex (str " Exception: " ex)))))))

(defn invalid-constructor-error-fn [color-mode]
  (fn [[?create-test-runner project-names]]
    (let [->error-message (error-message-fn color-mode project-names)]
      (when-let [message (invalid-constructor-message ?create-test-runner ->error-message)]
        (util/ordered-map
         :type "error"
         :code 109
         :message (color/clean-colors message)
         :colorized-message message
         :create-test-runner ?create-test-runner
         :projects (vec project-names))))))

(defn errors [{:keys [projects]} color-mode]
  (->> (for [[k v] (group-by #(-> % val :test :create-test-runner) projects)]
         [k (sort (mapv key v))])
       (into [] (keep (invalid-constructor-error-fn color-mode)))))
