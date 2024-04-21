(ns ^:no-doc polylith.clj.core.workspace.enrich.profile
  (:require [polylith.clj.core.workspace.enrich.external-ws-brick :as ws-brick]))

(defn profile [{:keys [lib-deps] :as profile} configs workspaces]
  (let [enriched (last (ws-brick/convert-libs-to-bricks lib-deps configs workspaces))]
    (assoc profile :lib-deps enriched)))

(defn enrich [profiles configs workspaces]
  (mapv #(profile % configs workspaces)
        profiles))
