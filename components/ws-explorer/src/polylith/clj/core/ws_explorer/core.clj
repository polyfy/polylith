(ns polylith.clj.core.ws-explorer.core
  (:require [clojure.pprint :as pp]
            [clojure.walk :as walk]
            [polylith.clj.core.util.interface :as util]
            [puget.printer :as puget]))

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
          (vec (sort (map #(-> % name str) (keys m)))))))))

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
    {:dev? :dev-q
     :run-tests? :run-tests-q
     :show-loc? :show-loc-q
     :show-lib? :show-lib-q
     :run-all-brick-tests? :run-all-brick-tests-q
     :run-env-tests? :run-env-tests-q
     :show-resources? :show-resources-q
     :ns->lib :ns-to-lib
     :env->alias :env-to-alias
     :env->indirect-changes :env-to-indirect-changes
     :env->bricks-to-test :env-to-bricks-to-test
     :env->environments-to-test :env-to-environments-to-test
     :profile->settings :profile-to-settings} workspace))

(defn extract [workspace get]
  (let [value (extract-value (replace-keys workspace)
                             (if (or (nil? get)
                                     (sequential? get)) get [get]))]
    (if (map? value)
      (into (sorted-map) value)
      value)))

(defn print-ws [workspace get]
  (puget/cprint (extract workspace get) color-schema))
