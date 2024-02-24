(ns polylith.clj.core.validator.m110-missing-config-file-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.m110-invalid-config-file :as m110]))

(deftest errors--in-current-and-other-workspaces
  (is (= [{:type "error",
           :code 110,
           :message "Missing config file: deps.edn",
           :colorized-message "Missing config file: deps.edn"}
          {:type "error",
           :code 110,
           :message "Validation error in ./deps.edn: [\"invalid type\"]. Found in workspace with alias 's'.",
           :colorized-message "Validation error in ./deps.edn: [\"invalid type\"]. Found in workspace with alias 's'."}]
         (m110/errors [{:error "Missing config file: deps.edn"}]
                      [{:config-error "Validation error in ./deps.edn: [\"invalid type\"]", :alias "s"}]))))
