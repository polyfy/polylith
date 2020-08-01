(ns polylith.clj.core.create.workspace
  (:require [polylith.clj.core.create.environment :as env]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.git.interfc :as git]))

(defn readme-content [ws-name]
  ["<img src=\"logo.png\" width=\"30%\" alt=\"Polylith\" id=\"logo\">"
   ""
   "The Polylith documentation can be found here:"
   ""
   "- The [high-level documentation](https://polylith.gitbook.io/polylith)"
   "- The [Polylith Tool documentation](https://github.com/tengstrand/polylith/tree/core)"
   "- The [RealWorld example app documentation](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app)"
   ""
   "You can also get in touch with the Polylith Team via our [forum](https://polylith.freeflarum.com) or on [Slack](https://clojurians.slack.com/archives/C013B7MQHJQ)."
   ""
   (str "<h1>" ws-name "</h1>")
   ""
   "<p>Add your workspace documentation here...</p>"])

(defn deps-content [ws-ns]
  [(str "{:polylith {:vcs \"git\"")
   (str "            :top-namespace \"" ws-ns "\"")
   (str "            :interface-ns \"interface\"")
   (str "            :env-aliases {\"development\" \"dev\"}}")
   (str " :paths [; Development")
   (str "         \"development/src\"")
   (str "")
   (str "         ; Components")
   (str "")
   (str "         ; Bases")
   (str "")
   (str "         ; Environments")
   (str "         ]")
   (str " :deps {org.clojure/clojure {:mvn/version \"1.10.1\"}")
   (str "        org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}")
   (str "")
   (str " :aliases  {:test {:extra-paths [; Components")
   (str "")
   (str "                                 ; Bases")
   (str "")
   (str "                                 ; Environments")
   (str "                                ]}")
   (str "")
   (str "            ; Polylith Tool")
   (str "            :poly {:extra-deps {tengstrand/polylith")
   (str "                                {:git/url   \"https://github.com/tengstrand/polylith.git\"")
   (str "                                 :sha       \"" (git/current-sha ".") "\"")
   (str "                                 :deps/root \"environments/dev\"}}}")
   (str "")
   (str "            :poly-check  {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"check\"]}")
   (str "            :poly-deps   {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"deps\"]}")
   (str "            :poly-help   {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"help\"]}")
   (str "            :poly-info   {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"info\"]}")
   (str "            :poly-test   {:main-opts [\"-m\" \"polylith.clj.core.cli.poly\" \"test\"]}}}")])

(defn create [current-dir ws-name ws-namespace]
  (if (nil? ws-namespace)
    (println "A namespace must be given.")
    (let [ws-path (str current-dir "/" ws-name)
          ws-ns (if (= "-" ws-namespace) "" ws-namespace)]
      (file/create-dir ws-path)
      (file/create-dir (str ws-path "/bases"))
      (file/create-dir (str ws-path "/components"))
      (file/create-dir (str ws-path "/development"))
      (file/create-dir (str ws-path "/development/src"))
      (file/create-dir (str ws-path "/environments"))
      (file/create-file (str ws-path "/deps.edn") (deps-content ws-ns))
      (file/create-file (str ws-path "/readme.md") (readme-content ws-name))
      (file/copy-resource-file! "create/logo.png" (str ws-path "/logo.png"))
      (git/init current-dir))))
