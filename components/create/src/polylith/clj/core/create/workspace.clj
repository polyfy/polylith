(ns polylith.clj.core.create.workspace
  (:require [polylith.clj.core.file.interfc :as file]
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

(defn deps-content [ws-ns]
  [(str "")
   (str "{:polylith {:vcs \"git\"")
   (str "            :top-namespace \"" ws-ns "\"")
   (str "            :interface-ns \"interface\"")
   (str "            :env->alias {\"development\" \"dev\"}")
   (str "            :ns->lib {clojure            org.clojure/clojure")
   (str "                      clojure.tools.deps org.clojure/tools.deps.alpha}}")
   (str "")
   (str " :aliases  {:dev {:extra-paths [\"development/src\"]")
   (str "                  :extra-deps {org.clojure/clojure {:mvn/version \"1.10.1\"}")
   (str "                               org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}}")
   (str "")
   (str "            :test {:extra-paths []}}}")])

(defn create-user-config-if-not-exists []
  (let [home-dir (user-config/home-dir)
        user-config-file (str home-dir "/.polylith/config.edn")]
    (when (-> user-config-file file/exists not)
      (file/create-missing-dirs user-config-file)
      (file/create-file user-config-file (user-config-content)))))

(defn create [ws-dir ws-name ws-namespace]
  (if (nil? ws-namespace)
    (println "  A namespace must be given.")
    (let [ws-path (str ws-dir "/" ws-name)]
      (file/create-dir ws-path)
      (file/create-dir (str ws-path "/bases"))
      (file/create-dir (str ws-path "/components"))
      (file/create-dir (str ws-path "/development"))
      (file/create-dir (str ws-path "/development/src"))
      (file/create-dir (str ws-path "/environments"))
      (file/create-file (str ws-path "/deps.edn") (deps-content ws-namespace))
      (file/create-file (str ws-path "/readme.md") (readme-content ws-name))
      (file/copy-resource-file! "create/logo.png" (str ws-path "/logo.png"))
      (create-user-config-if-not-exists)
      (git/init ws-path))))
