(ns ^:no-doc polylith.clj.core.validator.m107-missing-bricks-in-project
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.validator.shared :as shared]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color])
  (:refer-clojure :exclude [bases]))

(defn colored-entities [entity-names type color-mode]
  (str/join ", " (map #(color/entity type % color-mode)
                      entity-names)))

(defn missing-bricks-error [project-name interface-and-brick-names all-base-names test? color-mode]
  (let [base-names (vec (sort (filterv #(contains? all-base-names %) interface-and-brick-names)))
        interface-names (vec (sort (set/difference (set interface-and-brick-names) (set base-names))))
        bases-msg (colored-entities base-names :base color-mode)
        project-msg (str (color/project project-name color-mode) " project")
        interfaces-msg (colored-entities interface-names :interface color-mode)
        test-context-msg (if test? (str ", for the test context" (if (seq interface-names)
                                                                   "," ""))
                                   "")
        message (if (seq interface-names)
                  (str "Missing components in the " project-msg test-context-msg
                       " for these interfaces: " interfaces-msg
                       (if (seq base-names)
                         (str ", and these bases: " bases-msg)
                         ""))
                  (str "Missing bases in the " project-msg test-context-msg
                       ": " bases-msg))]
    [(util/ordered-map :type "error"
                       :code 107
                       :message (color/clean-colors message)
                       :colorized-message message
                       :interfaces interface-names
                       :bases base-names
                       :project project-name)]))

(defn missing-components [deps source]
  (let [missing-ifc-and-bases (mapcat #(-> % second source :missing-ifc-and-bases) deps)]
    (set (mapcat val missing-ifc-and-bases))))

(defn project-error [{:keys [name deps]} base-names color-mode]
  (let [missing (vec (sort (missing-components deps :src)))
        missing-test (vec (sort (missing-components deps :test)))]
    (cond
      (seq missing) (missing-bricks-error name missing base-names false color-mode)
      (seq missing-test) (missing-bricks-error name missing-test base-names true color-mode))))

(defn errors [cmd {:keys [active-profiles]} profiles bases projects color-mode]
  (when (shared/show-error? cmd profiles active-profiles)
    (let [base-names (set (map :name bases))]
      (mapcat #(project-error % base-names color-mode)
              projects))))