(ns polylith.clj.core.path-finder.interface.criterias
  (:require [clojure.string :as str]))

(defn truthy [_]
  true)

(defn =name [entity-name]
  (fn [entry] (= entity-name (:name entry))))

(defn component? [{:keys [type]}]
  (= :component type))

(defn base? [{:keys [type]}]
  (= :base type))

(defn brick? [{:keys [type]}]
  (or (= :base type)
      (= :component type)))

(defn project? [{:keys [type]}]
  (= :project type))

(defn src? [{:keys [test?]}]
  (not test?))

(defn test? [{:keys [test?]}]
  test?)

(defn exists? [{:keys [exists?]}]
  exists?)

(defn not-exists? [{:keys [exists?]}]
  (not exists?))

(defn src-path? [{:keys [path]}]
  (str/ends-with? path "/src"))

(defn test-path? [{:keys [path]}]
  (str/ends-with? path "/test"))

(defn resources-path? [{:keys [path]}]
  (str/ends-with? path "/resources"))

(defn not-test-or-resources-path [entry]
  (and (not (test-path? entry))
       (not (resources-path? entry))))

(defn profile? [{:keys [profile?]}]
  profile?)

(defn not-profile? [{:keys [profile?]}]
  (not profile?))

(defn match? [path-entry criterias]
  (every? true? ((apply juxt criterias) path-entry)))

(defn filter-entries [path-entries criterias]
  (if (empty? criterias)
    (vec path-entries)
    (filterv #(match? % criterias) path-entries)))

(defn has-entry? [path-entries criterias]
  (seq (filter-entries path-entries criterias)))
