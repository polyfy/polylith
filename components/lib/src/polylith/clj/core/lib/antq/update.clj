(ns ^:no-doc polylith.clj.core.lib.antq.update
  (:require [polylith.clj.core.antq.ifc :as antq]))

(defn update-lib? [dep]
  (-> dep second :latest-version))

(defn libs [{:keys [src test]}]
  (concat (filter update-lib? src)
          (filter update-lib? test)))

(defn update-entity [ws-dir color-mode {:keys [type name lib-deps project-lib-deps]}]
  (let [libraries (if (= "project" type)
                    (libs project-lib-deps)
                    (libs lib-deps))]
    (doseq [lib libraries]
      (antq/upgrade-lib ws-dir color-mode type name lib))))

(defn update-libs! [{:keys [ws-dir settings bases components projects]}]
  (let [color-mode (:color-mode settings)]
    (doseq [entity (concat bases components projects)]
      (update-entity ws-dir color-mode entity))))
