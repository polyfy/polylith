(ns ^:no-doc polylith.clj.core.workspace.text-table.profile
  (:require [polylith.clj.core.common.interface :as common]))

(defn inactive-profiles [{:keys [active-profiles default-profile-name]} profiles]
  (common/sort-profiles default-profile-name
                        (filter #(not (contains? (set active-profiles) (:name %)))
                                profiles)))
