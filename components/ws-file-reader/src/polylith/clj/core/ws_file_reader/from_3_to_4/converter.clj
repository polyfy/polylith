(ns ^:no-doc polylith.clj.core.ws-file-reader.from-3-to-4.converter)

(defn convert
  "We forgot to increase the ws number to 4.
   We know it's version 4 if it has the :configs -> :workspaces key."
  [{:keys [configs] :as workspace}]
  (assoc workspace :configs (-> configs (assoc :workspace (-> workspace :configs :workspaces first))
                                (dissoc :workspaces))))
