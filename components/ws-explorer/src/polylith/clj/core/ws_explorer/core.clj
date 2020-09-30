(ns polylith.clj.core.ws-explorer.core
  (:require [clojure.walk :as walk]
            [puget.printer :as puget]
            [polylith.clj.core.util.interface :as util]))

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

(defn value-from-map [m key]
  (let [k (keyword key)]
    (if (contains? m k)
      (m k)
      (if (contains? m key)
        (m key)
        (when (= "keys" key)
          (vec (sort (keys m))))))))

(defn value-from-vector [v index-or-name]
  (let [i (intify index-or-name)]
    (if (integer? i)
      (v i)
      (if-let [value (util/find-first #(and (map? %)
                                            (or (= index-or-name (:name %))
                                                (= index-or-name (:alias %)))) v)]
        value
        (when (= "keys" index-or-name)
          (mapv :name v))))))

(defn extract-value [value keys]
  (cond
    (empty? keys) value
    (and (counted? value) (= "count" (first keys))) (count value)
    (map? value) (recur (value-from-map value (first keys)) (rest keys))
    (vector? value) (recur (value-from-vector value (first keys)) (rest keys))
    :else value))

(defn replace-keys [workspace]
  ;; When getting values within the workspace data structure from the command line,
  ;; the > characters will pipe the result to a file and the ? character will not
  ;; work either, so therefore we replace keys containing those characters.
  (walk/postwalk-replace
    {:env->indirect-changes :env--indirect-changes
     :env->bricks-to-test :env--bricks-to-test
     :env->environments-to-test :env--environments-to-test
     :profile->settings :profile--settings} workspace))

(defn extract [workspace get]
  (let [value (extract-value (replace-keys workspace)
                             (if (or (nil? get)
                                     (sequential? get)) get [get]))]
    (if (map? value)
      (into (sorted-map) value)
      value)))

(defn print-ws [workspace get]
  (puget/cprint (extract workspace get) color-schema))
