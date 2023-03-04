(ns polylith.clj.core.validator.m107-missing-componens-in-project
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.validator.shared :as shared]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color])
  (:refer-clojure :exclude [bases]))

(defn missing-components-error [project-name interface-names test? color-mode]
  (let [interfaces (str/join ", " interface-names)
        message (str "Missing components in the " (color/project project-name color-mode) " project"
                     (if test? ", for the test context," "")
                     " for these interfaces: " (color/interface interfaces color-mode))]
    [(util/ordered-map :type "error"
                       :code 107
                       :message (color/clean-colors message)
                       :colorized-message message
                       :interfaces interface-names
                       :project project-name)]))

(defn missing-components [deps src-type]
  (let [missing-ifc (mapcat #(-> % second src-type :missing-ifc) deps)]
    (set (mapcat val missing-ifc))))

(defn project-error [{:keys [name deps]} color-mode]
  (let [missing (vec (sort (missing-components deps :src)))
        missing-test (vec (sort (missing-components deps :test)))]
    (cond
      (seq missing) (missing-components-error name missing false color-mode)
      (seq missing-test) (missing-components-error name missing-test true color-mode))))

(defn errors [cmd {:keys [profile-to-settings active-profiles]} projects color-mode]
  (when (shared/show-error? cmd profile-to-settings active-profiles)
    (mapcat #(project-error % color-mode)
            projects)))
