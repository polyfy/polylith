(ns project.poly.packaged-dependencies-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.workspace.interface :as workspace]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]))

(defn workspace [& args]
  (-> (user-input/extract-arguments (concat ["info" (str "ws-dir:.") "color-mode:none"] args))
      workspace/workspace))

(deftest clojure-test-test-runner-is-shipped-with-poly
  (is (-> (workspace)
          (ws-explorer/extract ["projects" "poly" "component-names" "src"])
          (set)
          (contains? "clojure-test-test-runner"))))
