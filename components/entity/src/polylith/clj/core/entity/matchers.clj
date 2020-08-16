(ns polylith.clj.core.entity.matchers
  (:require [clojure.string :as str]))

(defn =name [entity-name]
  (fn [entry] (= entity-name (:name entry))))

(defn =component [{:keys [type]}]
  (= :component type))

(defn =base [{:keys [type]}]
  (= :base type))

(defn =brick [{:keys [type]}]
  (or (= :base type)
      (= :component type)))

(defn =environment [{:keys [type]}]
  (= :environment type))

(defn =src [{:keys [test?]}]
  (not test?))

(defn =test [{:keys [test?]}]
  test?)

(defn =exists [{:keys [exists?]}]
  exists?)

(defn =src-path [{:keys [path]}]
  (str/ends-with? path "/src"))

(defn =resources-path [{:keys [path]}]
  (str/ends-with? path "/resources"))

(defn =test-path [{:keys [path]}]
  (str/ends-with? path "/test"))

(defn =profile [{:keys [profile?]}]
  profile?)

(defn =standard [{:keys [profile?]}]
  (not profile?))


(defn match? [path-entry criterias]
  (every? true? ((apply juxt criterias) path-entry)))

(defn filter-entries [path-entries criterias]
  (vec (filter #(match? % criterias) path-entries)))
