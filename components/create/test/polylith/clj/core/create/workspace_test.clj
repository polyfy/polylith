(ns polylith.clj.core.create.workspace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interfc :as helper]
            [polylith.clj.core.git.interfc :as git]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-workspace--missing-namespace--prints-out-error-message
  (let [ws-name "ws1"
        output (with-out-str
                 (helper/execute-command "" "create" "w" ws-name))]
    (is (= "A namespace must be given.\n"
           output))))

(deftest create-workspace--creates-empty-directories-and-a-deps-edn-config-file
  (let [ws-name "ws1"
        output (with-out-str
                 (helper/execute-command "" "create" "w" ws-name "se.example"))]
    (is (= ""
           output))

    (is (= #{"bases"
             "components"
             "development"
             "development/src"
             "environments"
             ".git"
             "logo.png"
             "deps.edn"
             "readme.md"}
           (helper/paths ws-name)))

    (is (= ["<img src=\"logo.png\" width=\"30%\" alt=\"Polylith\" id=\"logo\">"
            ""
            "The Polylith documentation can be found here:"
            ""
            "- The [high-level documentation](https://polylith.gitbook.io/polylith)"
            "- The [Polylith Tool documentation](https://github.com/tengstrand/polylith/tree/core)"
            "- The [RealWorld example app documentation](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app)"
            ""
            "You can also get in touch with the Polylith Team via our [forum](https://polylith.freeflarum.com) or on [Slack](https://clojurians.slack.com/archives/C013B7MQHJQ)."
            ""
            "<h1>ws1</h1>"
            ""
            "<p>Add your workspace documentation here...</p>"]
           (helper/content ws-name "readme.md")))

    (is (= ["{:polylith {:vcs \"git\""
            "            :top-namespace \"se.example\""
            "            :interface-ns \"interface\""
            "            :env-aliases {\"development\" \"dev\"}}"
            " :paths [; Development"
            "         \"development/src\""
            ""
            "         ; Components"
            ""
            "         ; Bases"
            ""
            "         ; Environments"
            "         ]"
            " :deps {org.clojure/clojure {:mvn/version \"1.10.1\"}"
            "        org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}"
            ""
            " :aliases  {:test {:extra-paths [; Components"
            ""
            "                                 ; Bases"
            ""
            "                                 ; Environments"
            "                                ]}"
            ""
            "            ; Polylith Tool"
            "            :poly {:extra-deps {tengstrand/polylith"
            "                                {:git/url   \"https://github.com/tengstrand/polylith.git\""
            "                                 :sha       \"21f40507a24291ead2409ce33277378bb7e94ac6\""
            "                                 :deps/root \"environments/dev\"}}}"
            ""
            "            :poly-check  {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"check\"]}"
            "            :poly-deps   {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"deps\"]}"
            "            :poly-help   {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"help\"]}"
            "            :poly-info   {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"info\"]}"
            "            :poly-test   {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"test\"]}}}"]
           (helper/content ws-name "deps.edn")))))

(deftest create-workspace--creates-workspace-with-an-empty-top-namespace
  (let [ws-name "ws1"
        output (with-out-str
                 (helper/execute-command "" "create" "w" ws-name "-"))]
    (is (= ""
           output))

    (is (= #{"bases"
             "components"
             "development"
             "development/src"
             "environments"
             ".git"
             "logo.png"
             "deps.edn"
             "readme.md"}
           (helper/paths ws-name)))

    (is (= ["{:polylith {:vcs \"git\""
            "            :top-namespace \"\""
            "            :interface-ns \"interface\""
            "            :env-aliases {\"development\" \"dev\"}}"
            " :paths [; Development"
            "         \"development/src\""
            ""
            "         ; Components"
            ""
            "         ; Bases"
            ""
            "         ; Environments"
            "         ]"
            " :deps {org.clojure/clojure {:mvn/version \"1.10.1\"}"
            "        org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}"
            ""
            " :aliases  {:test {:extra-paths [; Components"
            ""
            "                                 ; Bases"
            ""
            "                                 ; Environments"
            "                                ]}"
            ""
            "            ; Polylith Tool"
            "            :poly {:extra-deps {tengstrand/polylith"
            "                                {:git/url   \"https://github.com/tengstrand/polylith.git\""
            "                                 :sha       \"21f40507a24291ead2409ce33277378bb7e94ac6\""
            "                                 :deps/root \"environments/dev\"}}}"
            ""
            "            :poly-check  {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"check\"]}"
            "            :poly-deps   {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"deps\"]}"
            "            :poly-help   {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"help\"]}"
            "            :poly-info   {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"info\"]}"
            "            :poly-test   {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"test\"]}}}"]
           (helper/content ws-name "deps.edn")))))
