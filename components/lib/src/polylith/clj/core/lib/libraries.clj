(ns ^:no-doc polylith.clj.core.lib.libraries)

(defn lib [[name {:keys [version size type]}]]
  [(cond-> {:name name
            :version (or version "-")}
           size (assoc :size size)
           type (assoc :type type))])

(defn entity-lib [[name {:keys [brick] :as entity}]]
  (if (nil? brick)
    (lib [name entity])
    []))

(defn profile-lib [{:keys [lib-deps]}]
  (mapcat entity-lib lib-deps))

(defn indirect-libs
  "Transitive-only library rows collected from projects' ':indirect-lib-deps'
   (populated when ':transitive' is passed), restricted to libraries that are
   also declared directly somewhere in the workspace (`declared-names`). This
   keeps the focus on libraries where a declared version and a transitively
   resolved version can drift apart (issue #613), and filters out pure plumbing
   like 'org.clojure/spec.alpha' that no brick declares."
  [projects declared-names]
  (set (filter #(contains? declared-names (:name %))
               (mapcat (fn [{:keys [indirect-lib-deps]}]
                         (mapcat lib (concat (:src indirect-lib-deps)
                                             (:test indirect-lib-deps))))
                       projects))))

(defn with-inconsistent-lib-version [{:keys [name] :as library}
                                     multi-version-lib-names
                                     excluded-libs]
  (if (and (contains? multi-version-lib-names name)
           (not (contains? excluded-libs name)))
    (assoc library :inconsistent-lib-version true)
    library))

(defn libs
  ([workspace] (libs workspace false))
  ([{:keys [configs profiles components bases projects]} include-indirect?]
  (let [entities (concat components bases projects)
        src-libs (set (concat (mapcat entity-lib (mapcat #(-> % :lib-deps :src) entities))
                              (mapcat profile-lib profiles)))
        test-libs (set (mapcat lib (mapcat #(-> % :lib-deps :test) entities)))
        indirect-libs (if include-indirect?
                        (indirect-libs projects (set (map :name (concat src-libs test-libs))))
                        #{})
        all-libs (set (concat src-libs test-libs indirect-libs))
        excluded-libs (set (map str (-> configs :workspace :validations :inconsistent-lib-versions :exclude)))
        multi-version-lib-names (set (map ffirst
                                          (filter #(> (-> % second count) 1)
                                                  (group-by (juxt :name :type)
                                                            all-libs))))
        libraries (vec (sort-by (juxt :name :version)
                                (map #(with-inconsistent-lib-version % multi-version-lib-names excluded-libs)
                                     all-libs)))]
    {:libraries libraries
     :src-libs src-libs})))

(defn used-libraries [workspace]
  (:libraries (libs workspace)))

(comment
  (require '[dev.jocke :as dev])
  (:libraries (libs dev/workspace))
  (used-libraries dev/workspace)
  #__)
