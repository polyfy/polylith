(ns polylith.clj.core.workspace.settings-enricher)

(defn enrich [settings {:keys [active-dev-profiles]}]
  (assoc settings :active-dev-profiles active-dev-profiles))
