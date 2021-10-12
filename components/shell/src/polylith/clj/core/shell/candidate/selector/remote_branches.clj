(ns polylith.clj.core.shell.candidate.selector.remote-branches
  (:require [clojure.string :as str]
            [polylith.clj.core.sh.interface :as sh]
            [polylith.clj.core.shell.candidate.creators :as c]))

(defn branch [branch-str]
  (let [branch (last (str/split (str/trim branch-str) #" "))]
    (when (str/starts-with? branch "origin/")
      [(subs branch 7)])))

(defn branches []
  (let [{:keys [exit out]} (sh/execute-with-return "git" "branch" "-r")]
    (if (zero? exit)
      (vec (sort (mapcat branch (str/split-lines out))))
      [])))

(defn select [candidate _ _]
  (let [group-id (-> candidate :group :id)]
    (mapv #(c/group-arg % group-id "branch")
          (branches))))
