(ns polylith.clj.core.deps.lib-deps
  (:require [clojure.tools.deps.alpha :as tools-deps]))

(defn adjust-key [{:keys [type path version git/url sha exclusions]}]
  (case type
    "maven" {:mvn/version version :exclusions (vec exclusions)}
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
  "Convert back to tools.deps format."
  [workspace {:keys [lib-deps maven-repos]}]
  (assoc workspace :mvn/repos maven-repos
                   :deps (into {} (map key-as-symbol (merge (:src lib-deps)
                                                            (:test lib-deps))))))

(defn resolve-deps [workspace project]
  "Resolves what library versions that are used by the given project."
  (let [config (->config workspace project)
        deps (:deps config)]
    (tools-deps/resolve-deps config {:extra-deps deps})))

(defn name-version [[k {:keys [mvn/version]}]]
  [(str k) version])
