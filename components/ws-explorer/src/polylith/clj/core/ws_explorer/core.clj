(ns polylith.clj.core.ws-explorer.core
  (:require [clojure.pprint :as pp]
            [clojure.string :as str]
            [puget.printer :as puget]
            [clojure.walk :as walk]
            [polylith.clj.core.user-config.interface :as user-config]
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

(defn replace-home-fn [system-user-home user-home]
  (fn [value] (if (and (string? value)
                       (str/starts-with? value system-user-home))
                (str/replace value system-user-home user-home)
                value)))

(defn replace-user-home [value user-home]
  "If :user-home is passed in to the poly command, replace strings that
   starts with the USER_HOME environment variable with USER-HOME."
  (let [system-user-home (user-config/home-dir)
        replace-home (replace-home-fn system-user-home user-home)]
    (if user-home
      (walk/postwalk replace-home value)
      value)))

(defn extract [workspace get]
  (let [user-home (-> workspace :user-input :user-home)
        value (-> (extract-value workspace
                                 (if (or (nil? get)
                                         (sequential? get)) get [get]))
                  (replace-user-home user-home))]
    (if (map? value)
      (into (sorted-map) value)
      value)))

(defn ws [workspace get out color-mode]
  (if (nil? out)
    (if (= color/none color-mode)
      (pp/pprint (extract workspace get))
      (puget/cprint (extract workspace get) color-schema))
    (pp/pprint (extract workspace get) (clojure.java.io/writer out))))
