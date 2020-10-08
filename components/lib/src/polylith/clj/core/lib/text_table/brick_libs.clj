(ns polylith.clj.core.lib.text-table.brick-libs)

(defn with-brick-name [brick-name lib-name lib-name->lib]
  (when-let [lib (lib-name->lib lib-name)]
    (assoc lib :brick brick-name)))

(defn brick-lib [brick-name brick->lib-names lib-name->lib]
  (filter identity (mapv #(with-brick-name brick-name % lib-name->lib)
                         (brick->lib-names brick-name))))

(defn lib-name-lib [[name lib]]
  [name (assoc lib :name name)])

(defn brick-libs [{:keys [component-names base-names lib-deps]} brick->lib-names]
  (let [brick-names (concat component-names base-names)
        lib-name->lib (into {} (map lib-name-lib lib-deps))]
    (mapcat #(brick-lib % brick->lib-names lib-name->lib) brick-names)))

(defn libs-keys [[brick libs]]
  [brick (map #(select-keys % [:name :version :size]) libs)])

(defn brick->libs [environments bricks profile-to-settings]
  (let [brick->lib-names (into {} (map (juxt :name :lib-dep-names) bricks))]
    (into {} (map libs-keys
                  (group-by :brick
                            (set (mapcat #(brick-libs % brick->lib-names)
                                         (concat environments (vals profile-to-settings)))))))))
