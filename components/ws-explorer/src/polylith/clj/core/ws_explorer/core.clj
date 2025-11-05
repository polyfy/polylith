(ns ^:no-doc polylith.clj.core.ws-explorer.core
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [puget.printer :as puget]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.str :as str-util]))

(def color-schema
  {:color-scheme {:nil       [:magenta]
                  :number    [:yellow]
                  :string    [:yellow]
                  :boolean    [:magenta]
                  :keyword    [:magenta]
                  :delimiter  [:white]}})

(defn intify [arg]
  (try
    (Integer/parseUnsignedInt arg)
    (catch Exception _
      arg)))

(defn keys? [key]
  (contains? #{"" "keys"} key))

(defn search? [key-name]
  (str/ends-with? key-name "*"))

(defn match-str? [value key-name]
  (when value
    (if (keys? key-name)
      true
      (if (search? key-name)
        (str/starts-with? value (str-util/drop-last 1 key-name))
        (= value key-name)))))

(defn value-from-map [m key-name]
  (let [k (keyword key-name)
        mm (into {} m)]
    (cond
      (contains? mm k) (mm k)
      (contains? mm key-name) (mm key-name)
      (search? key-name) (mapv keyword
                               (filter #(match-str? % key-name)
                                       (map name (keys mm)))))))

(defn match? [value key-name]
  (or (and (map? value)
           (or (match-str? (:name value) key-name)
               (match-str? (:alias value) key-name)))
      (and (string? value)
           (match-str? value key-name))
      (and (keyword? value)
           (match-str? (name value) key-name))))

(defn value-from-vector [v index-or-name]
  (let [i (intify index-or-name)]
    (if (integer? i)
      (v i)
      (let [values (filterv #(match? % index-or-name) v)]
        (if (= 1 (count values))
          (if (search? index-or-name)
            values
            (first values))
          values)))))

(defn vector-key [value]
  (if (map? value)
    (:name value)
    value))

(defn keys-value [value]
  (cond
    (map? value) (vec (sort (keys value)))
    (vector? value) (mapv vector-key value)))

(defn extract-value [value keys]
  (let [key (first keys)]
    (cond
      (nil? key) value
      (contains? #{"" "keys"} key) (recur (keys-value value) (rest keys))
      (= "count" key) (when (counted? value) (count value))
      :else (cond
              (map? value) (recur (value-from-map value key) (rest keys))
              (vector? value) (recur (value-from-vector value key) (rest keys))
              :else value))))

(defn do-replace [value {:keys [from to]}]
  (if (string? value)
    (str/replace value (re-pattern from) to)
    value))

(defn replace-fn [replace]
  (fn [value] (reduce do-replace value replace)))

(defn replace-values [value replace]
  (if replace
    (walk/postwalk (replace-fn replace) value)
    value))

(defn extract [edn-data values]
  (let [replace (-> edn-data :user-input :replace)
        value (-> (extract-value edn-data values)
                  (replace-values replace))]
    (if (map? value)
      (into (sorted-map) value)
      value)))

(defn adjust-keys [get]
  (let [values (if (or (nil? get)
                     (sequential? get)) get [get])]
    (if (= "" (last values))
      (drop-last values)
      values)))

(defn ws [workspace get out color-mode]
  (let [values (adjust-keys get)
        extracted (extract workspace values)
        ;; Normalize for file output to ensure EDN can be read back
        normalized (if out (util/sanitize-keywords extracted) extracted)]
    (if (nil? out)
      (if (= color/none color-mode)
        (pp/pprint extracted)
        (puget/cprint extracted color-schema))
      (pp/pprint normalized (io/writer out)))))
