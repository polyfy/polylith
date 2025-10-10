(ns polylith.clj.core.creator.workspace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.test-helper.interface :as helper]
            [polylith.clj.core.creator.workspace :as workspace]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-workspace--when-workspace-already-exists--return-error-message
  (let [output (str-util/normalize-newline
                 (with-out-str
                   (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example")
                   (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example")))]
    (is (= "  Workspace 'ws1' already exists.\n"
           output))))

(deftest create-workspace--within-another-workspace-as-cljs
  (let [output (str-util/normalize-newline
                 (with-out-str
                   (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example" "dialects:cljs" ":commit")
                   (helper/execute-command "ws1" "create" "workspace" "name:ws2" "top-ns:com.example")))]
    (is (= "  Workspace created in existing git repo.\n"
           output))

    (is (= #{".git"
             ".gitignore"
             ".vscode"
             ".vscode/settings.json"
             "bases"
             "bases/.keep"
             "components"
             "components/.keep"
             "deps.edn"
             "development"
             "development/src"
             "development/src/.keep"
             "logo.png"
             "package.json"
             "projects"
             "projects/.keep"
             "readme.md"
             "workspace.edn"
             "ws2"
             "ws2/.gitignore"
             "ws2/.vscode"
             "ws2/.vscode/settings.json"
             "ws2/bases"
             "ws2/bases/.keep"
             "ws2/components"
             "ws2/components/.keep"
             "ws2/deps.edn"
             "ws2/development"
             "ws2/development/src"
             "ws2/development/src/.keep"
             "ws2/logo.png"
             "ws2/projects"
             "ws2/projects/.keep"
             "ws2/readme.md"
             "ws2/workspace.edn"}
           (helper/paths "ws1")))))

(deftest create-workspace--incorrect-first-argument--prints-out-error-message
  (let [output (str-util/normalize-newline
                 (with-out-str
                   (helper/execute-command "" "create" "x" "name:ws1")))]
    (is (= "  The first argument after 'create' is expected to be any of: base, component, project, workspace.\n"
           output))))

(deftest create-workspace--missing-top-namespace--prints-out-error-message
  (let [output (str-util/normalize-newline
                 (with-out-str
                   (helper/execute-command "" "create" "workspace" "name:ws1")))]
    (is (= "  A top namespace must be given, e.g.: create workspace name:my-workspace top-ns:com.my-company\n"
           output))))

(deftest create-workspace--creates-empty-directories-and-a-deps-edn-config-file
  (let [output (with-redefs [workspace/mvn-version (fn [] "0.2.18")]
                 (with-out-str
                   (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example" "branch:create-deps-files" ":commit")))]
    (is (= ""
           output))

    (is (= #{".git"
             ".gitignore"
             ".vscode"
             ".vscode/settings.json"
             "bases"
             "bases/.keep"
             "components"
             "components/.keep"
             "deps.edn"
             "development"
             "development/src"
             "development/src/.keep"
             "logo.png"
             "projects"
             "projects/.keep"
             "readme.md"
             "workspace.edn"}
           (helper/paths "ws1")))

    (is (= ["<img src=\"logo.png\" width=\"30%\" alt=\"Polylith\" id=\"logo\">"
            ""
            "The Polylith documentation can be found here:"
            ""
            "- The [high-level documentation](https://polylith.gitbook.io/polylith)"
            "- The [poly tool documentation](https://cljdoc.org/d/polylith/clj-poly/CURRENT)"
            "- The [RealWorld example app documentation](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app/tree/cljs-frontend)"
            ""
            "You can also get in touch with the Polylith Team on [Slack](https://clojurians.slack.com/archives/C013B7MQHJQ)."
            ""
            "<h1>ws1</h1>"
            ""
            "<p>Add your workspace documentation here...</p>"]
           (helper/content "ws1" "readme.md")))

    (is (= ["{:aliases  {:dev {:extra-paths [\"development/src\"]"
            ""
            "                  :extra-deps {org.clojure/clojure {:mvn/version \"1.12.2\"}}}"
            ""
            "            :test {:extra-paths []}"
            ""
            "            :poly {:main-opts [\"-m\" \"polylith.clj.core.poly-cli.core\"]"
            "                   :jvm-opts [\"--enable-native-access=ALL-UNNAMED\"]"
            "                   :extra-deps {polylith/clj-poly {:mvn/version \"0.2.18\"}}}}}"]
           (helper/content "ws1" "deps.edn")))

    (is (= ["{"
            "    \"calva.replConnectSequences\": ["
            "        {"
            "            \"projectType\": \"deps.edn\","
            "            \"name\": \"ws1\","
            "            \"cljsType\": \"none\","
            "            \"menuSelections\": {"
            "                \"cljAliases\": [\"dev\", \"test\", \"+default\"]"
            "            }"
            "        }"
            "    ]"
            "}"]
           (helper/content "ws1" ".vscode/settings.json")))

    (is (= ["{:top-namespace \"se.example\""
            " :interface-ns \"interface\""
            " :default-profile-name \"default\""
            " :dialects [\"clj\"]"
            " :compact-views #{}"
            " :vcs {:name \"git\""
            "       :auto-add false}"
            " :tag-patterns {:stable \"^stable-.*\""
            "                :release \"^v[0-9].*\"}"
            " :template-data {:clojure-ver \"1.12.3\""
            "                 :shadow-cljs-ver \"^3.2.1\"}"
            " :validations {:inconsistent-lib-versions {:type :warning"
            "                                           :exclude []}}"
            " :projects {\"development\" {:alias \"dev\"}}}"]
           (helper/content "ws1" "workspace.edn")))

    ;; no env vars checked in helper so use defaul XDG location:
    (is (= [:color-mode
            :empty-character
            :thousand-separator]
           (keys
             (helper/content-data
               (helper/user-home) "/.config/polylith/config.edn"))))))
