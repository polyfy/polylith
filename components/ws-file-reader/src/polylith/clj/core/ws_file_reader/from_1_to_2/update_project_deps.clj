(ns ^:no-doc polylith.clj.core.ws-file-reader.from-1-to-2.update-project-deps)

(defn source-dep [{:keys [direct indirect circular direct-ifc missing-ifc]}]
  (let [missing-ifc-and-bases (or direct-ifc missing-ifc)]
    (cond-> {:direct {:src direct :test []}
             :indirect {:src indirect :test []}}
            circular (assoc :circular circular)
            missing-ifc-and-bases (assoc :missing-ifc-and-bases missing-ifc-and-bases))))

(defn brick-dep [[brick-name {:keys [src test] :as dep}]]
  (if src
    [brick-name {:src (source-dep src)
                 :test (source-dep test)}]
    [brick-name {:src (source-dep dep)
                 :test []}]))

(defn brick-deps [brick-deps]
  (into {} (map brick-dep brick-deps)))

(defn update-deps [{:keys [deps] :as brick}]
  (assoc brick :deps (brick-deps deps)))

(defn convert [{:keys [projects] :as workspace}]
  (assoc workspace :projects (mapv update-deps projects)))
