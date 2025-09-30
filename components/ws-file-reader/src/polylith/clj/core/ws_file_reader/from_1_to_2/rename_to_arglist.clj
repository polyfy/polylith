(ns ^:no-doc polylith.clj.core.ws-file-reader.from-1-to-2.rename-to-arglist)

(defn rename [definition]
  (clojure.set/rename-keys definition {:parameters :arglist}))

(defn convert-interface [{:keys [definitions] :as interface}]
  (assoc interface :definitions (mapv rename definitions)))

(defn convert [{:keys [interfaces] :as ws}]
  (assoc ws :interfaces (mapv convert-interface interfaces)))
