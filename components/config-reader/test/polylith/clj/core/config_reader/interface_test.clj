(ns polylith.clj.core.config-reader.interface-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.config-reader.interface :as config-reader]))

(deftest read-deps-file--substitute-aliases-in-paths
  (is (= {:paths ["clj" "cljc" "resources"],
          :deps {},
          :aliases {:src-paths ["clj" "cljc"],
                    :resources-path ["resources"],
                    :test-paths ["test" "test2"],
                    :test {:extra-paths ["test" "test2"], :extra-deps {}}}}
         (:config (config-reader/read-deps-file "examples/for-test/components/company/deps.edn" "deps.edn")))))

(deftest read-brick-config-files--ignore-bases-without-config-files
  (is (= ["mybase"]
         (mapv :name
               (first (config-reader/read-brick-config-files "examples/missing-component" :toolsdeps2 "base"))))))

(deftest read-brick-config-files--ignore-components-without-config-files
  (is (= ["mycomp"]
         (mapv :name
               (first (config-reader/read-brick-config-files "examples/missing-component" :toolsdeps2 "component"))))))

(deftest read-project-config-files--ignore-components-without-config-files
  (is (= ["development" "service"]
         (mapv :name
               (first (config-reader/read-project-config-files "examples/missing-component" :toolsdeps2))))))
