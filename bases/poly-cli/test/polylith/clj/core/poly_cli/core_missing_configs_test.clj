(ns polylith.clj.core.poly-cli.core-missing-configs-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.config-reader.interface :as config-reader]
            [polylith.clj.core.workspace-clj.ws-config :as ws-config]
            [polylith.clj.core.poly-cli.core :as cli]))

(deftest check-a-valid-workspace
  (let [output (read-string (with-out-str
                              (cli/-main "ws" "get:ws-type" "ws-dir:examples/local-dep" "color-mode:none" ":no-exit")))]
    (is (= "toolsdeps2"
           output))
    (is (= 0
           (cli/-main "check" "ws-dir:examples/local-dep" ":no-exit")))))

(deftest check-an-old-valid-workspace
  (let [output (read-string (with-out-str
                              (cli/-main "ws" "get:ws-type" "ws-dir:examples/local-dep-old-format" "color-mode:none" ":no-exit")))]
    (is (= "toolsdeps1"
           output))
    (is (= (cli/-main "check" "ws-dir:examples/local-dep-old-format" ":no-exit")
           0))))

(deftest check-a-workspace-with-missing-workspace-config-file
  (let [check-fn (fn [] (with-redefs [config-reader/file-exists? (fn [_ type] (not= :workspace type))
                                      validator/validate-project-dev-config (fn [_ _ _] "Invalid file")
                                      ws-config/ws-config-from-disk (fn [_] [nil "Missing workspace config"])]
                          (cli/-main "check" "ws-dir:examples/local-dep" "color-mode:none" ":no-exit")))
        output (with-out-str (check-fn))]
    (is (= "  Missing workspace config\n"
           output))
    (is (= 1
           (check-fn)))))

(deftest check-a-workspace-with-missing-development-config-file
  (let [check-fn (fn [] (with-redefs [config-reader/file-exists? (fn [_ type] (not= :development type))
                                      validator/validate-project-dev-config (fn [_ _ _] "Missing file")]
                          (cli/-main "check" "ws-dir:examples/local-dep" "color-mode:none" ":no-exit")))
        output (with-out-str (check-fn))]
    (is (= "  Missing file\n"
           output))
    (is (= 1
           (check-fn)))))

(deftest check-a-workspace-with-missing-project-config-file
  (let [check-fn (fn [] (with-redefs [validator/validate-project-deployable-config (fn [_ _ _] "Invalid file")]
                          (cli/-main "check" "ws-dir:examples/local-dep" "color-mode:none" ":no-exit")))
        output (with-out-str (check-fn))]
    (is (= "  Error 110: Invalid file\n"
           output))
    (is (= 110
           (check-fn)))))

(deftest check-a-workspace-with-illegal-component-config-file
  (let [check-fn (fn [] (with-redefs [validator/validate-brick-config (fn [_ _ deps-filename]
                                                                        (when (= deps-filename "components/without-src/deps.edn")
                                                                          "Invalid file"))]
                          (cli/-main "check" "ws-dir:examples/local-dep" "color-mode:none" ":no-exit")))
        output (with-out-str (check-fn))]
    (is (= "  Error 110: Invalid file\n"
           output))
    (is (= 110
           (check-fn)))))
