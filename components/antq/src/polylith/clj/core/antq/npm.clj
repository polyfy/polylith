(ns ^:no-doc polylith.clj.core.antq.npm
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(defn keyword->full-name
  "Convert a keyword to its full string name, preserving namespace for scoped npm packages.
   For :@mantine/core, returns '@mantine/core' instead of just 'core'."
  [kw]
  (if-let [ns (namespace kw)]
    (str ns "/" (name kw))
    (name kw)))

(defn npm-registry-url [package-name]
  (str "https://registry.npmjs.org/" package-name))

(defn get-latest-version [package-name]
  "Get the latest version of an npm package from the npm registry"
  (try
    (let [pkg-name (if (keyword? package-name) (keyword->full-name package-name) (str package-name))
          response (http/get (npm-registry-url pkg-name) {:accept :json})
          body (json/parse-string (:body response) true)
          latest-version (get-in body [:dist-tags :latest])]
      latest-version)
    (catch Exception _)))

(defn npm-dep? [[k value]]
  (and (not= "@poly" (namespace k))
       (not= "*" value)))

(defn npm-dependencies->latest-versions [npm-deps]
  "Convert npm dependencies to latest version information"
  (into {} (map (fn [[name]]
                  (let [pkg-name (cond
                                   (keyword? name) (keyword->full-name name)
                                   (string? name) name
                                   :else (str name))]
                    (when-let [latest-version (get-latest-version name)]
                      [pkg-name latest-version])))
                (filter npm-dep? npm-deps))))

(defn outdated-npm-dependencies [npm-deps]
  "Find npm dependencies that are outdated (current version != latest version)"
  (let [latest-versions (npm-dependencies->latest-versions npm-deps)]
    (into {} (keep (fn [[pkg-key current-version]]
                     (let [pkg-name (keyword->full-name pkg-key)
                           latest-version (get latest-versions pkg-name)]
                       (when (and latest-version
                                  (not= latest-version current-version))
                         [pkg-name latest-version])))
                   (filter npm-dep? npm-deps)))))

(defn read-package-json [file-path]
  "Read and parse package.json file"
  (try
    (json/parse-string (slurp file-path) true)
    (catch Exception e
      (println (str "Error reading package.json: " (.getMessage e)))
      nil)))

(defn write-package-json [file-path package-data]
  "Write package.json file with proper formatting"
  (try
    (spit file-path (json/generate-string package-data {:pretty true}))
    true
    (catch Exception e
      (println (str "Error writing package.json: " (.getMessage e)))
      false)))

(defn update-npm-dependency [file-path package-name new-version]
  "Update a specific npm dependency in package.json"
  (if-let [package-data (read-package-json file-path)]
    (let [package-key (keyword package-name)
          updated-data (cond
                         (contains? (:dependencies package-data) package-key)
                         (update package-data :dependencies assoc package-key new-version)
                         (contains? (:devDependencies package-data) package-key)
                         (update package-data :devDependencies assoc package-key new-version)
                         :else (update package-data :dependencies assoc package-key new-version))]
      (write-package-json file-path updated-data))
    false))

(defn upgrade-npm-deps! [upgrade-specs]
  "Upgrade npm dependencies in package.json files"
  (let [results (map (fn [{:keys [file dependency]}]
                        (let [{:keys [name latest-version]} dependency
                              file-path file]
                          (update-npm-dependency file-path name latest-version)))
                     upgrade-specs)]
    (into {} (map vector upgrade-specs results))))
