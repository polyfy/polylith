(ns polylith.clj.core.poly-cli.core-missing-configs-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.config-reader.interface :as config-reader]
            [polylith.clj.core.workspace-clj.ws-config :as ws-config]
            [polylith.clj.core.poly-cli.core :as cli]))

(deftest check-a-valid-workspace
  (let [output (read-string (with-out-str
                              (cli/-main "ws" "get:ws-type" "ws-dir:examples/local-dep" "color-mode:none" ":no-exit")))]
    (is (= output "toolsdeps2"))
    (is (= (cli/-main "check" "ws-dir:examples/local-dep" ":no-exit")
           0))))

(deftest check-an-old-valid-workspace
  (let [output (read-string (with-out-str
                              (cli/-main "ws" "get:ws-type" "ws-dir:examples/local-dep-old-format" "color-mode:none" ":no-exit")))]
    (is (= output "toolsdeps1"))
    (is (= (cli/-main "check" "ws-dir:examples/local-dep-old-format" ":no-exit")
           0))))

(deftest check-a-workspace-with-missing-workspace-config-file
  (let [check-fn (fn [] (with-redefs [config-reader/file-exists? (fn [_ type] (not= :workspace type))
                                      validator/validate-project-dev-config (fn [_ _ _] "Invalid file")
                                      ws-config/ws-config-from-disk (fn [_] [nil "Missing workspace"])]
                          (cli/-main "check" "ws-dir:examples/local-dep" "color-mode:none" ":no-exit")))
        output (with-out-str (check-fn))]
    (is (= output "  Missing workspace\n"))
    (is (= (check-fn) 1))))

(deftest check-a-workspace-with-missing-development-config-file
  (let [check-fn (fn [] (with-redefs [config-reader/file-exists? (fn [_ type] (not= :development type))
                                      validator/validate-project-dev-config (fn [_ _ _] "Missing file")]
                          (cli/-main "check" "ws-dir:examples/local-dep" "color-mode:none" ":no-exit")))
        output (with-out-str (check-fn))]
    (is (= output "  Missing file\n"))
    (is (= (check-fn) 1))))
