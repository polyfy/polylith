(ns ^:no-doc polylith.clj.core.shell.candidate.shared
  (:require [polylith.clj.core.util.interface.color :as color]))

(defn parent-groups [{:keys [candidates]}]
  (concat (filter :top-group-id candidates)
          (mapcat parent-groups candidates)))

(defn group-entry [{:keys [top-group-id] :as candidate}]
  [top-group-id (into {} (map (juxt :parsed-value identity)
                              (:candidates candidate)))])

(defn groups [candidates]
  (into {} (map group-entry (parent-groups {:candidates candidates}))))

(defn args [groups {:keys [id param]}]
  (get-in groups [id param :args]))

(defn colored-brick [brick-name base-names color-mode]
  (if (contains? base-names brick-name)
    (color/base brick-name color-mode)
    (color/component brick-name color-mode)))
