(ns polylith.clj.core.git.tag
  (:require [clojure.string :as str]
            [polylith.clj.core.shell.interface :as shell]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn log-lines [ws-dir]
  (str/split-lines (shell/sh "git" "log" (str "--pretty=format:%H %d") :dir ws-dir)))

(defn skip-tag [string]
  (if (str/starts-with? string "tag: ")
    (subs string 5)
    string))

(defn matching-tag [tag pattern]
  (when (re-find (re-pattern pattern) tag)
    tag))

(defn tags [line pattern]
  (when-let [string (str-util/take-until
                      (str-util/skip-until line "tag: ") ")")]
    (when-let [tag (first (filter identity
                                  (map #(matching-tag (skip-tag %) pattern)
                                       (str/split string #", "))))]
      {:tag tag
       :sha (subs line 0 40)})))

(defn matching-tags [ws-dir pattern]
  (filterv identity (map #(tags % pattern)
                         (log-lines ws-dir))))
