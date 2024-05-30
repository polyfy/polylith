(ns ^:no-doc polylith.clj.core.workspace.enrich.profile
  (:require [polylith.clj.core.workspace.enrich.external-ws-brick :as ws-brick]))

(defn profile [{:keys [lib-deps] :as profile} ws-dir workspaces]
  (let [enriched (last (ws-brick/convert-libs-to-bricks lib-deps ws-dir workspaces))]
    (assoc profile :lib-deps enriched)))

(defn enrich [profiles ws-dir workspaces]
  (mapv #(profile % ws-dir workspaces)
        profiles))
