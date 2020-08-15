(ns polylith.clj.core.entity.interfc
  (:require [polylith.clj.core.entity.path-extractor :as extractor])
  (:require [polylith.clj.core.entity.path-selector :as selector]))

(defn path-infos [ws-dir src-paths test-paths profile-src-paths profile-test-paths]
  (extractor/path-infos ws-dir src-paths test-paths profile-src-paths profile-test-paths))

(defn src-paths [entity-src->path-infos]
  (selector/all-src-paths entity-src->path-infos))

(defn test-paths [entity-src->path-infos]
  (selector/all-test-paths entity-src->path-infos))

(defn brick->src-paths [entity-src->path-infos]
  (selector/brick->src-paths entity-src->path-infos))

(defn brick->test-paths [entity-src->path-infos]
  (selector/brick->test-paths entity-src->path-infos))

(defn env->src-paths [entity-src->path-infos]
  (selector/env->src-paths entity-src->path-infos))

(defn env->test-paths [entity-src->path-infos]
  (selector/env->test-paths entity-src->path-infos))
