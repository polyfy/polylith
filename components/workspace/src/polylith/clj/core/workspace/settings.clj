(ns polylith.clj.core.workspace.settings
  (:require [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.util.interface :as util]))

(defn enrich [ws-dir {:keys [stable-since-tag-pattern] :as settings}]
  (let [{:keys [tag sha]}
        (if (git/is-git-repo? ws-dir)
          (git/latest-stable ws-dir stable-since-tag-pattern)
          {:sha ""})]

    (assoc settings :stable-since (util/ordered-map :sha sha
                                                    :tag tag))))
