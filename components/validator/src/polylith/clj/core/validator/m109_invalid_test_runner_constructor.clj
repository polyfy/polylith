(ns polylith.clj.core.validator.m109-invalid-test-runner-constructor
  (:require [clojure.string :as str]
            [polylith.clj.core.test-runner-plugin-init.interface :as runner-init]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn invalid-ctor? [ctor-var]
  (and (some? ctor-var)
       (not (runner-init/valid-constructor-var? ctor-var))))

(defn error-or-maybe-ctor-var [ctor-spec ->error-message]
  (try {:ctor-var (runner-init/->constructor-var ctor-spec)}
       (catch Exception e
         {:error (->error-message (str "Unable to load test runner constructor " ctor-spec) e)})))

(defn invalid-ctor-message [ctor-spec ->error-message]
  (let [{:keys [ctor-var error]} (error-or-maybe-ctor-var ctor-spec ->error-message)]
    (or error
        (when (invalid-ctor? ctor-var)
          (->error-message (str "The var referred to by " ctor-spec " is not a valid test runner constructor"))))))

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

(defn invalid-ctor-error-fn [color-mode]
  (fn [[ctor-spec project-names]]
    (let [->error-message (error-message-fn color-mode project-names)]
      (when-let [message (invalid-ctor-message ctor-spec ->error-message)]
        (util/ordered-map
         :type "error"
         :code 109
         :message (color/clean-colors message)
         :colorized-message message
         :make-test-runner ctor-spec
         :projects (vec project-names))))))

(defn errors [{:keys [projects]} color-mode]
  (->> (for [[k v] (group-by #(-> % val :test :make-test-runner) projects)]
         [k (sort (mapv key v))])
       (into [] (keep (invalid-ctor-error-fn color-mode)))))
