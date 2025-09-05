(ns ^:no-doc polylith.clj.core.antq.outdated
  (:require [antq.api :as antq]
            [polylith.clj.core.maven.interface :as maven]))

(defn truncate [version type]
  (if (= :git-sha type)
    (subs version 0 7)
    version))

(defn key-value [{:keys [name latest-version type]}]
  [name
   (truncate latest-version type)])

(defn entity-configs [configs]
  (let [{:keys [bases components projects]} configs]
    (concat bases components projects)))

(defn oldest-lib-version [result [lib v2]]
  (if (:mvn/version v2)
    (let [v1 (result lib v2)
          v (maven/oldest-lib v1 v2 :mvn/version)]
      (assoc result lib v))
    result))

(defn dependencies [{:keys [name type deps]}]
  (if (and (= "project" type)
           (= "development" name))
    (concat (-> deps :aliases :dev :extra-deps)
            (-> deps :aliases :test :extra-deps))
    (:deps deps)))

(defn npm-dependencies [{:keys [package]}]
  (when package
    (merge (:dependencies package)
           (:devDependencies package))))

(defn library->latest-version
  "Returns a map where the key is [lib-name lib-version]
   and the value is the latest version of the library."
  [configs calculate?]
  (if calculate?
    (let [maven-deps (into {} (reduce oldest-lib-version {}
                                      (mapcat dependencies
                                              (entity-configs configs))))
          npm-deps (into {} (mapcat npm-dependencies
                                    (entity-configs configs)))
          ;; Get maven latest versions using antq
          maven-latest-versions (into {} (map key-value)
                                      (antq/outdated-deps {:deps maven-deps}))
          ;; Get npm latest versions using our custom npm namespace (if available)
          npm-latest-versions (try
                                (require 'polylith.clj.core.antq.npm)
                                ((resolve 'polylith.clj.core.antq.npm/npm-dependencies->latest-versions) npm-deps)
                                (catch Exception e
                                  (println "Warning: npm dependency checking not available:" (.getMessage e))
                                  {}))]
      (merge maven-latest-versions npm-latest-versions))
    {}))
