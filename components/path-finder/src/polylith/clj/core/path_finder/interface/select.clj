(ns polylith.clj.core.path-finder.interface.select
  (:require [polylith.clj.core.path-finder.selector :as select]))

(defn entries [path-entries & criterias]
  (select/entries path-entries criterias))

(defn exists? [path-entries & criterias]
  (select/exists? path-entries criterias))

(defn lib-deps [dep-entries & criterias]
  (select/lib-deps dep-entries criterias))

(defn paths [path-entries & criterias]
  (select/paths path-entries criterias))

(defn names [path-entries & criterias]
  (select/names path-entries criterias))
