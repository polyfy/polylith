(ns polylith.clj.core.creator.workspace
  (:require [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.git.interfc :as git]
            [polylith.clj.core.user-config.interfc :as user-config]))

(defn user-config-content []
  ["{:color-mode \"dark\""
   " :thousand-separator \",\"}"])

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

(defn deps-content [top-ns]
  [(str "")
   (str "{:polylith {:vcs \"git\"")
   (str "            :top-namespace \"" top-ns "\"")
   (str "            :interface-ns \"interface\"")
   (str "            :stable-since-tag-pattern \"stable-*\"")
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
   (str "            :poly {:main-opts [\"-m\" \"polylith.clj.core.poly_cli.poly\"]")
   (str "                   :extra-deps {tengstrand/polylith")
   (str "                                {:git/url   \"https://github.com/tengstrand/polylith.git\"")
   (str "                                 :sha       \"" common/poly-git-sha "\"")
   (str "                                 :deps/root \"environments/cli\"}}}}}")])

(defn create-user-config-if-not-exists []
  (let [home-dir (user-config/home-dir)
        user-config-file (str home-dir "/.polylith/config.edn")]
    (when (-> user-config-file file/exists not)
      (file/create-missing-dirs user-config-file)
      (file/create-file user-config-file (user-config-content)))))

(defn create [root-dir ws-name top-ns]
  (let [ws-dir (str root-dir "/" ws-name)]
    (file/create-dir ws-dir)
    (file/create-dir (str ws-dir "/bases"))
    (file/create-dir (str ws-dir "/components"))
    (file/create-dir (str ws-dir "/development"))
    (file/create-dir (str ws-dir "/development/src"))
    (file/create-dir (str ws-dir "/environments"))
    (file/create-file (str ws-dir "/deps.edn") (deps-content top-ns))
    (file/create-file (str ws-dir "/readme.md") (readme-content ws-name))
    (file/create-file (str ws-dir "/development/src/.keep") [""])
    (file/copy-resource-file! "creator/logo.png" (str ws-dir "/logo.png"))
    (create-user-config-if-not-exists)
    (git/init ws-dir)))
