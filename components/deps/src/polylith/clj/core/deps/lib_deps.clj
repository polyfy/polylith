(ns polylith.clj.core.deps.lib-deps
  (:require [clojure.tools.deps.alpha :as tools-deps]))

(defn name-version [[k {:keys [mvn/version]}]]
  [(str k) version])

(defn adjust-key [{:keys [type path version git/url sha exclusions]}]
  (case type
    "maven" {:mvn/version version
             :exclusions (vec exclusions)}
    "local" {:local/root path}
    "git"   {:git/url url :sha sha}
    (throw (Exception. (str "Unknown library type: " type)))))

(defn key-as-symbol
  "The library names (keys) are stored as strings in the workspace
   and need to be converted back to symbols here.
   Library dependencies are stored as :type and :version and need
   to be translated back to :mvn/version and :local/root."
  [[library version]]
  [(symbol library) (adjust-key version)])

(defn ->config
  "Converts back to the tools.deps format.
   Tools.deps only resolves src depenencies (:deps) but not test
   dependencies (:aliases > :test > :extra-deps) which is the reason
   we merge :src and :test."
  [{:keys [lib-deps maven-repos]}]
  {:mvn/repos maven-repos
   :deps (into {} (map key-as-symbol (merge (:src lib-deps)
                                            (:test lib-deps))))})
(defn resolve-deps [project is-verbose]
  "Resolves which library versions that are used by the given project."
  (let [config (->config project)
        _ (when is-verbose (println (str "# config=" config)))]
    (tools-deps/resolve-deps config {})))
