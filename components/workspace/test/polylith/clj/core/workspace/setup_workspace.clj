(ns polylith.clj.core.workspace.setup-workspace
  (:require [clojure.java.shell :as shell]
            [clojure.test :refer :all]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.test-helper.interface :as helper]))

(defn execute-command [{:keys [cmd args clean-result-fn] :as command}]
  (println (str "Execute test: " cmd " " args))
  (let [result (with-out-str
                 (apply helper/execute-command
                        (concat ["ws03" cmd "color-mode:none" "fake-sha:1234567"] args)))]
    (assoc command
           :actual (if clean-result-fn
                     (clean-result-fn result)
                     result))))

(defn resolve-deps [ws-dir]
  (shell/sh "clojure" "-A:dev:test" "-P" :dir ws-dir))

(defn execute-commands [commands]
  (helper/execute-command "" "create" "w" "name:ws03" "top-ns:se.example" ":git-add" ":commit")
  (helper/execute-command "ws03" "create" "c" "name:database1" "interface:database")
  (helper/execute-command "ws03" "create" "c" "name:test-helper1" "interface:test-helper")
  (helper/execute-command "ws03" "create" "c" "name:admin" "interface:user")
  (helper/execute-command "ws03" "create" "c" "name:user1" "interface:user")
  (helper/execute-command "ws03" "create" "c" "name:util1" "interface:util")
  (helper/execute-command "ws03" "create" "p" "name:service")

  (let [ws-dir (str (helper/root-dir) "/ws03")
        ;; components/admin
        _ (file/create-file (str ws-dir "/components/admin/src/se/example/user/interface.clj")
                            ["(ns se.example.user.interface)"
                             ""
                             "(def value 42)"])
        ;; components/database1
        _ (file/create-file (str ws-dir "/components/database1/src/se/example/database/interface.clj")
                            ["(ns se.example.database.interface"
                             "  (:require [se.example.util.interface :as util]))"])
        ;; components/test-helper1
        _ (file/create-file (str ws-dir "/components/test-helper1/src/se/example/test_helper/interface.clj")
                            ["(ns se.example.test-helper.interface)"
                             ""
                             "(defn do-stuff [])"])
        _ (file/create-file (str ws-dir "/components/test-helper1/test/se/example/test_helper/interface_test.clj")
                            ["(ns se.example.test-helper.interface-test"
                             "  (:require [clojure.test :as test :refer :all]"
                             "            [se.example.test-helper.interface :as test-helper]))"])
        _ (file/create-file (str ws-dir "/components/test-helper1/deps.edn")
                            ["{:paths [\"src\" \"resources\"]"
                             " :deps {metosin/malli {:mvn/version \"0.5.0\"}}"
                             " :aliases {:test {:extra-paths [\"test\"]"
                             "                  :extra-deps {}}}}"])
        ;; components/user1
        _ (file/create-file (str ws-dir "/components/user1/src/se/example/user/interface.clj")
                            ["(ns se.example.user.interface)"
                             ""
                             "(def value 123)"])
        _ (file/create-file (str ws-dir "/components/user1/test/se/example/user/interface_test.clj")
                            ["(ns se.example.user.interface-test"
                             "  (:require [clojure.test :as test :refer :all]"
                             "            [se.example.user.interface :as user]"
                             "            [se.example.database.stuff :as db-stuff]"
                             "            [se.example.test-helper.interface :as test-helper]))"])
        ;; projects/service
        _ (file/create-file (str ws-dir "/projects/service/deps.edn")
                            ["{:deps {poly/user1 {:local/root \"../../components/user1\"}"
                             "        poly/util1 {:local/root \"../../components/util1\"}"
                             "        poly/database1 {:local/root \"../../components/database1\"}"
                             "        org.clojure/clojure {:mvn/version \"1.10.1\"}"
                             "        org.clojure/tools.deps.alpha {:mvn/version \"0.12.985\"}}"
                             " :aliases {:test {:extra-paths []"
                             "                  :extra-deps  {poly/test-helper1 {:local/root \"../../components/test-helper1\"}}}}}"])
        ;; deps.edn
        _ (file/create-file (str ws-dir "/deps.edn")
                            ["{:aliases  {:dev {:extra-paths [\"development/src\""
                             "                                \"components/database1/src\""
                             "                                \"components/util1/src\"]"
                             "                  :extra-deps {org.clojure/clojure {:mvn/version \"1.10.1\"}"
                             "                               metosin/malli {:mvn/version \"0.5.0\"}"
                             "                               org.clojure/tools.deps.alpha {:mvn/version \"0.12.985\"}}}"
                             ""
                             "            :+default {:extra-paths [\"components/user1/src\""
                             "                                     \"components/user1/test\"]"
                             "                       :extra-deps {me.raynes/fs {:mvn/version \"1.4.6\"}}}"
                             "            :+extra {:extra-paths [\"components/admin/src\""
                             "                                   \"components/admin/test\"]"
                             "                     :extra-deps {}}"
                             ""
                             "            :test {:extra-paths [\"components/database1/test\""
                             "                                 \"components/test-helper1/src\""
                             "                                 \"components/test-helper1/test\""
                             "                                 \"components/util1/test\"]}}}"])
        ;; workspace.edn
        _ (file/create-file (str ws-dir "/workspace.edn")
                            ["{:top-namespace \"se.example\""
                             " :interface-ns \"interface\""
                             " :default-profile-name \"default\""
                             " :compact-views #{}"
                             " :vcs {:name \"git\""
                             "       :auto-add true}"
                             " :tag-patterns {:stable \"stable-*\""
                             "                :release \"v[0-9]*\"}"
                             " :projects {\"development\" {:alias \"dev\"}"
                             "            \"service\" {:alias \"s\"}}}"])]
    (resolve-deps ws-dir)

    (mapv execute-command commands)))
