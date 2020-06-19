(ns polylith.workspace-clj.environment
  (:require [clojure.string :as str]))

(defn test? [key-name]
  (str/ends-with? key-name "-test"))

(defn env? [[key] prefix]
  (= prefix (namespace key)))

(defn group [key-name]
  (if (test? key-name)
    (subs key-name 0 (- (count key-name) 5))
    key-name))

(defn base? [path]
  (and (string? path)
       (str/starts-with? path "bases/")))

(defn component? [path]
  (and (string? path)
       (str/starts-with? path "components/")))

(defn brick-name [path]
  (let [index1 (inc (str/index-of path "/"))
        path1 (subs path index1)
        index2 (str/index-of path1 "/")]
    (if (< index2 0)
      path1
      (subs path1 0 index2))))

(defn brick-names [paths brick?]
  (vec (sort (set (map brick-name (filter brick? paths))))))

(defn key-as-string [[lib version]]
  [(str lib) version])

(defn sort-deps [deps]
  (apply array-map
         (mapcat identity
                 (sort (map key-as-string deps)))))

(defn environment [[key {:keys [extra-paths extra-deps]
                         :or   {extra-paths []
                                extra-deps {}}}]
                   paths deps]
  (let [key-name (name key)
        all-paths (set (concat paths extra-paths))
        components (brick-names all-paths component?)
        bases (brick-names all-paths base?)
        sorted-deps (sort-deps (merge deps extra-deps))]
    {:name key-name
     :group (group key-name)
     :test? (test? key-name)
     :type "environment"
     :components components
     :bases bases
     :paths (vec (sort all-paths))
     :deps sorted-deps}))

(defn environments [env-prefix {:keys [paths deps aliases]}]
  (vec (sort-by (juxt :type :name)
                (mapv #(environment % paths deps)
                      (filter #(env? % env-prefix) aliases)))))
