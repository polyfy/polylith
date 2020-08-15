(ns polylith.clj.core.entity.path-selector)

(defn key= [[[category _ src-type]] compare-category compare-src-type]
  (and (or (= category compare-category)
           (nil? compare-category))
       (or (= src-type compare-src-type)
           (nil? compare-src-type))))

(defn entity-path [[[_ name] rows]]
  [name (set (map :path rows))])

(defn all-src-paths [entity-src->path-infos]
  (vec (sort (map :path
                  (filter #(-> % :test? not)
                          (mapcat second entity-src->path-infos))))))

(defn all-test-paths [entity-src->path-infos]
  (vec (sort (map :path
                  (filter :test?
                          (mapcat second entity-src->path-infos))))))

(defn- select-paths [category type entity-src->path-infos]
  (into {} (map entity-path
                (filter #(key= % category type) entity-src->path-infos))))

(defn brick->src-paths [entity-src->path-infos]
  (select-paths :brick :src entity-src->path-infos))

(defn brick->test-paths [entity-src->path-infos]
  (select-paths :brick :test entity-src->path-infos))

(defn env->src-paths [entity-src->path-infos]
  (select-paths :env :src entity-src->path-infos))

(defn env->test-paths [entity-src->path-infos]
  (select-paths :env :test entity-src->path-infos))
