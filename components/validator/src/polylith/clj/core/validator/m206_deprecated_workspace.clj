(ns polylith.clj.core.validator.m206-deprecated-workspace
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface :as util]))

(defn warnings [workspace]
  (if (common/need-migration? workspace)
    (let [message (str "This workspace is deprecated. As of version 0.2.19, "
                       "brick and project configurations are stored in config.edn files under each base/component/project. "
                       "Execute the 'migrate' command to migrate the workspace, or create those files manually and remove "
                       "the :bricks and :projects keys from workspace.edn to get rid of this warning.")]
      [(util/ordered-map :type "warning"
                         :code 206
                         :message message
                         :colorized-message message)])))
