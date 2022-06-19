(ns polylith.clj.core.migrator.project-deps
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interface.config :as config]
            [polylith.clj.core.migrator.shared :as shared]))

(defn local-root [brick-name brick-dir]
  [(symbol "poly" brick-name)
   {:local/root (str "../../" brick-dir "/" brick-name)}])

(defn local-roots [{:keys [src test]} src-key brick-dir]
  (vec (mapcat #(local-root % brick-dir)
               (if (= :src src-key)
                 src
                 (sort (set/difference (set test)
                                       (set src)))))))

(defn keep-key? [[k _] libs]
  (not (contains? libs k)))

(defn brick-libs [type bases components]
  (set (mapcat #(map symbol (-> % :lib-deps type keys))
               (concat components bases))))

(defn deps [components bases component-names base-names src-key deps-key-path content]
  (let [src-libs (brick-libs src-key bases components)]
    (apply array-map
           (concat (local-roots component-names src-key "components")
                   (local-roots base-names src-key "bases")
                   (mapcat identity (filter #(keep-key? % src-libs)
                                            (get-in content deps-key-path)))))))

(defn not-brick? [path]
  (and (not (str/starts-with? path "../../bases/"))
       (not (str/starts-with? path "../../components/"))))

(defn new-config [filename
                  {:keys [base-names component-names]}
                  {:keys [components bases]}]

  (let [content (config/read-deps-file filename)
        paths (:paths content)
        test-paths (-> :aliases content :test :extra-paths)
        new-paths (filterv not-brick? paths)
        new-test-paths (filterv not-brick? test-paths)
        src-deps (deps components bases component-names base-names :src [:deps] content)
        test-deps (deps components bases component-names base-names :test [:aliases :test :extra-deps] content)
        new-content (-> content
                        (assoc-in [:aliases :test :extra-paths] new-test-paths)
                        (assoc :deps src-deps)
                        (assoc-in [:aliases :test :extra-deps] test-deps))
        final-content (if (empty? new-paths)
                        (dissoc new-content :paths)
                        (assoc new-content :paths new-paths))]
    (shared/format-content filename
                           [:paths :deps :aliases]
                           final-content)))


(defn recreate-config-file [ws-dir {:keys [name] :as project} workspace]
  (let [project-path (str "projects/" name "/deps.edn")
        filename (str ws-dir "/" project-path)]
    (spit filename (new-config filename project workspace))
    (println (str " - " project-path " created"))))

(defn recreate-config-files [ws-dir {:keys [projects] :as workspace}]
  (doseq [project (filter #(-> % :is-dev not) projects)]
    (recreate-config-file ws-dir project workspace)))
