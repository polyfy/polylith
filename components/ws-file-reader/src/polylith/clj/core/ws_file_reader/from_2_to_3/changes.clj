(ns ^:no-doc polylith.clj.core.ws-file-reader.from-2-to-3.changes)

(defn with-settings [{:keys [name] :as project} changes]
  (let [{:keys [project-to-indirect-changes
                project-to-bricks-to-test
                project-to-projects-to-test]} changes
        indirect-changes (get project-to-indirect-changes name)
        bricks-to-test (get project-to-bricks-to-test name)
        projects-to-test (get project-to-projects-to-test name)]
    (cond-> project
            indirect-changes (assoc :indirect-changes indirect-changes)
            bricks-to-test (assoc :bricks-to-test bricks-to-test)
            projects-to-test (assoc :projects-to-test projects-to-test))))

(defn convert [{:keys [changes projects] :as workspace}]
  (-> workspace
      (assoc :projects (mapv #(with-settings % changes)
                             projects)
             :changes (dissoc changes :project-to-indirect-changes
                                      :project-to-bricks-to-test
                                      :project-to-projects-to-test))))
