(ns ^:no-doc polylith.clj.core.antq.npm
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(defn npm-registry-url [package-name]
  (str "https://registry.npmjs.org/" package-name))

(defn get-latest-version [package-name]
  "Get the latest version of an npm package from the npm registry"
  (try
    (let [pkg-name (if (keyword? package-name) (clojure.core/name package-name) (str package-name))
          response (http/get (npm-registry-url pkg-name) {:accept :json})
          body (json/parse-string (:body response) true)
          latest-version (get-in body [:dist-tags :latest])]
      latest-version)
    (catch Exception e
      (println (str "Error fetching npm package " package-name ": " (.getMessage e)))
      nil)))

(defn npm-dependencies->latest-versions [npm-deps]
  "Convert npm dependencies to latest version information"
  (into {} (map (fn [[name]]
                  (let [pkg-name (cond
                                   (keyword? name) (clojure.core/name name)
                                   (string? name) name
                                   :else (str name))]
                    (if-let [latest-version (get-latest-version name)]
                      [pkg-name latest-version]
                      [pkg-name nil])))
                npm-deps)))

(defn outdated-npm-dependencies [npm-deps]
  "Find npm dependencies that are outdated (current version != latest version)"
  (let [latest-versions (npm-dependencies->latest-versions npm-deps)]
    (into {} (filter (fn [[name latest-version]]
                       (and latest-version
                            (not= latest-version (npm-deps (keyword name)))))
                     latest-versions))))

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
