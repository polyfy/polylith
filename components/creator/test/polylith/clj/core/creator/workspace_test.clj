(ns polylith.clj.core.creator.workspace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.test-helper.interfc :as helper]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-workspace--trying-to-create-a-workspace-within-another-workspace--prints-out-error-messagex
  (let [output (with-out-str
                 (helper/execute-command "" "create" "w" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "workspace" "name:ws2" "top-ns:com.example"))]
    (is (= "  A workspace should not be created within another workspace.\n"
           output))))

(deftest create-workspace--incorrect-first-argument--prints-out-error-message
  (let [output (with-out-str
                 (helper/execute-command "" "create" "x" "name:ws1"))]
    (is (= "  The first argument after 'create' is expected to be any of: w, e, b, c, workspace, environment, base, component.\n"
           output))))

(deftest create-workspace--missing-top-namespace--prints-out-error-message
  (let [output (with-out-str
                 (helper/execute-command "" "create" "w" "name:ws1"))]
    (is (= "  A top namespace must be given, e.g.: create w name:my-workspace top-ns:com.my-company\n"
           output))))

(deftest create-workspace--creates-empty-directories-and-a-deps-edn-config-file
  (let [output (with-out-str
                 (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example"))]
    (is (= ""
           output))

    (is (= #{"bases"
             "components"
             "development"
             "development/src"
             "development/src/.keep"
             "environments"
             ".git"
             "logo.png"
             "deps.edn"
             "readme.md"}
           (helper/paths "ws1")))

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
           (helper/content "ws1" "readme.md")))

    (is (= [(str "")
            (str "{:polylith {:vcs \"git\"")
            (str "            :top-namespace \"se.example\"")
            (str "            :interface-ns \"interface\"")
            (str "            :env->alias {\"development\" \"dev\"}")
            (str "            :ns->lib {clojure             org.clojure/clojure")
            (str "                      clojure.tools.deps  org.clojure/tools.deps.alpha}}")
            (str "")
            (str " :aliases  {:dev {:extra-paths [\"development/src\"]")
            (str "                  :extra-deps {org.clojure/clojure {:mvn/version \"1.10.1\"}")
            (str "                               org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}}")
            (str "")
            (str "            :test {:extra-paths []}")
            (str "")
            (str "            :poly {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\"]")
            (str "                   :extra-deps {tengstrand/polylith")
            (str "                                {:git/url   \"https://github.com/tengstrand/polylith.git\"")
            (str "                                 :sha       \"" common/poly-git-sha "\"")
            (str "                                 :deps/root \"environments/cli\"}}}}}")]
           (helper/content "ws1" "deps.edn")))

    (is (= ["{:color-mode \"dark\""
            " :thousand-separator \",\"}"]
           (helper/content (helper/user-home) "/.polylith/config.edn")))))
