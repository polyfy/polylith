(ns polylith.environment.deps-edn
  (:require [clojure.string :as str]))

(def config '{:paths ["shared/src"]

              :polylith {:top-namespace "clojure.realworld"
                         :env-prefix "env"}

              :ring {:init clojure.realworld.rest-api.api/init
                     :destroy clojure.realworld.rest-api.api/destroy
                     :handler clojure.realworld.rest-api.api/app
                     :port 6003}

              :deps {clj-time {:mvn/version "0.14.2"}
                     org.clojure/clojure {:mvn/version "1.10.0"}
                     metosin/spec-tools {:mvn/version "0.6.1"}}

              :aliases {:dev {:extra-deps {tengstrand/polylith {:git/url "https://github.com/rtengstrand/polylith.git"
                                                                :sha "89a91b1c519b338eb5a15c90cb97559c09484e89"}}}

                        :env/realworld-backend {:extra-paths ["bases/build-tools/src"
                                                              "bases/rest-api/resources"
                                                              "bases/rest-api/src"
                                                              "components/article/src"
                                                              "components/article/resources"
                                                              "components/comment/src"
                                                              "components/comment/resources"
                                                              "components/database/src"
                                                              "components/database/resources"]

                                                :extra-deps  {clj-jwt                 {:mvn/version "0.1.1"}
                                                              com.taoensso/timbre     {:mvn/version "4.10.0"}
                                                              compojure/compojure     {:mvn/version "1.6.0"}
                                                              crypto-password         {:mvn/version "0.2.0"}}}

                        :env/realworld-backend-test {:extra-paths ["bases/rest-api/test"
                                                                   "include-me/test"
                                                                   "components/article/test"
                                                                   "components/comment/test"
                                                                   "components/database/test"]
                                                     :extra-deps  {org.clojure/test.check {:mvn/version "0.10.0-alpha3"}}}

                        :env/build-tools {:extra-paths ["bases/build-tools/src"]
                                          :extra-deps  {ring-server {:mvn/version "0.5.0"}}}


                        :env/build-tools-test {:extra-paths ["bases/build-tools/test"]
                                               :extra-deps  {org.clojure/test.check {:mvn/version "0.10.0-alpha3"}}}}})

(defn test? [key-name]
  (str/ends-with? key-name "-test"))

(defn env? [[key]]
  (= "env" (namespace key)))

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

(defn base [path]
  {:name (brick-name path)
   :type "base"})

(defn component [path]
  {:name (brick-name path)
   :type "component"})

(defn environment [[key {:keys [extra-paths extra-deps]
                         :or   {extra-paths []
                                extra-deps {}}}]
                   paths deps]
  (let [key-name (name key)
        all-paths (set (concat paths extra-paths))
        all-deps (merge deps extra-deps)
        components (set (map component (filter component? all-paths)))
        bases (set (map base (filter base? all-paths)))
        extra-paths (sort all-paths)]
    {:name key-name
     :group (group key-name)
     :test? (test? key-name)
     :components (vec (sort-by :name components))
     :bases (vec (sort-by :name bases))
     :extra-paths (vec extra-paths)
     :dependencies all-deps}))

(defn environments [{:keys [paths deps aliases]}]
  (vec (sort-by (juxt :type :name)
                (mapv #(environment % paths deps)
                      (filter env? aliases)))))

;(def paths (:paths config))
;(def deps (:deps config))
;(def aliases (:aliases config))
;(def env (second (first (filter env? aliases))))
;
;(environment env paths deps)
;(def extra-paths (-> env second :extra-paths env))
;(def all-paths (set (concat paths extra-paths)))

