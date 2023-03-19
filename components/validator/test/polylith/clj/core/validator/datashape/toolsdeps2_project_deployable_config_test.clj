(ns polylith.clj.core.validator.datashape.toolsdeps2-project-deployable-config-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.datashape.toolsdeps2 :as toolsdeps2]))

(def config {:deps '{poly/change {:local/root "../../components/change"}
                     poly/command {:local/root "../../components/command"}
                     poly/poly-cli {:local/root "../../bases/poly-cli"}
                     org.clojure/clojure {:mvn/version "1.10.1"}
                     org.clojure/tools.deps {:mvn/version "0.16.1264"}
                     clj-commons/fs {:mvn/version "1.6.310"}
                     mvxcvi/puget {:mvn/version "1.3.1"}}

             :aliases {:test      {:extra-paths ["test"]
                                   :extra-deps  {}}

                       :aot       {:extra-paths ["classes"]
                                     :main-opts ["-e" "(compile,'polylith.clj.core.poly-cli.core)"]}

                       :uberjar   {:extra-deps {'uberdeps {:mvn/version "0.1.10"}}
                                   :main-opts  ["-m" "uberdeps.uberjar"]}}})

(deftest validate-project-deployable-config--valid-config--returns-nil
  (is (= (toolsdeps2/validate-project-deployable-config config "deps.edn")
         nil)))

(deftest validate-project-deployable-config--valid-config-withoug-deps--returns-nil
  (is (= (toolsdeps2/validate-project-deployable-config (dissoc config :deps) "deps.edn")
         nil)))
