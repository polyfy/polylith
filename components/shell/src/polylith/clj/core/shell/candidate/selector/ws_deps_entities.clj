(ns ^:no-doc polylith.clj.core.shell.candidate.selector.ws-deps-entities
  (:require [clojure.set :as set]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.shell.candidate.shared :as shared]
            [polylith.clj.core.util.interface.color :as color]))

(defn brick-candidate [brick-name base-names {:keys [color-mode]}]
  (c/candidate brick-name
               (shared/colored-brick brick-name base-names color-mode)
               brick-name :remaining [{:group {:id :deps
                                               :param "brick"}}]))

(defn project-candidate [project-name {:keys [color-mode]}]
  (c/candidate project-name
               (color/project project-name color-mode)
               project-name :remaining [{:group {:id :deps
                                                 :param "project"}}]))

(defn project-brick-names [project]
  (set (concat (-> project :base-names :src)
               (-> project :base-names :test)
               (-> project :component-names :src)
               (-> project :component-names :test))))

(defn contains-bricks? [project brick-names]
  (seq (set/intersection (project-brick-names project)
                         (set brick-names))))

(defn select-projects [_ groups {:keys [settings projects]}]
  (let [project-names (vec (sort (map :name projects)))
        brick-names (shared/args groups {:id :deps, :param "brick"})]
    (mapv #(project-candidate % settings)
          (if (seq brick-names)
            (map :name (filter #(contains-bricks? % brick-names) projects))
            project-names))))

(defn select-bricks [_ groups {:keys [settings projects components bases]}]
  (let [base-names (set (map :name bases))
        project-name (first (shared/args groups {:id :deps, :param "project"}))]
    (mapv #(brick-candidate % base-names settings)
          (-> (if project-name
                (-> project-name
                    (common/find-project projects)
                    (project-brick-names))
                (map :name (concat components bases)))
              sort vec))))
