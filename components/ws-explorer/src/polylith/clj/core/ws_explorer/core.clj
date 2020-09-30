(ns polylith.clj.core.ws-explorer.core
  (:require [clojure.pprint :as pp]
            [puget.printer :as puget]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

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

(defn extract [workspace get]
  (let [value (extract-value workspace
                             (if (or (nil? get)
                                     (sequential? get)) get [get]))]
    (if (map? value)
      (into (sorted-map) value)
      value)))

(defn ws [workspace get out color-mode]
  (if (nil? out)
    (if (= color/none color-mode)
      (pp/pprint (extract workspace get))
      (puget/cprint (extract workspace get) color-schema))
    (pp/pprint (extract workspace get) (clojure.java.io/writer out))))
