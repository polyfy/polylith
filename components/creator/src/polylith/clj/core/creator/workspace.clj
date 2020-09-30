(ns polylith.clj.core.creator.workspace
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.user-config.interface :as user-config]
            [polylith.clj.core.util.interface.os :as os]))

(def gitignore-content
  ["**/classes"
   "**/target"])

(defn user-config-content []
  (let [empty-char (if (os/windows?) "." "·")
        color-mode (if (os/windows?) "none" "dark")]
    [(str "{:color-mode \"" color-mode "\"")
     (str " :empty-character \"" empty-char "\"")
     (str " :thousand-separator \",\"}")]))

(defn readme-content [ws-name]
  ["<img src=\"logo.png\" width=\"30%\" alt=\"Polylith\" id=\"logo\">"
   ""
   "The Polylith documentation can be found here:"
   ""
   "- The [high-level documentation](https://polylith.gitbook.io/polylith)"
   "- The [Polylith Tool documentation](https://github.com/polyfy/polylith)"
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
   (str "            :default-profile-name \"default\"")
   (str "            :build-tag-pattern \"v[0-9]*\"")
   (str "            :stable-since-tag-pattern \"stable-*\"")
   (str "            :env->alias {\"development\" \"dev\"}")
   (str "            :ns-to-lib {}}")
   (str "")
   (str " :aliases  {:dev {:extra-paths [\"development/src\"]")
   (str "                  :extra-deps {org.clojure/clojure {:mvn/version \"1.10.1\"}")
   (str "                               org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}}")
   (str "")
   (str "            :test {:extra-paths []}")
   (str "")
   (str "            :poly {:main-opts [\"-m\" \"polylith.clj.core.poly_cli.poly\"]")
   (str "                   :extra-deps {polyfy/polylith")
   (str "                                {:git/url   \"https://github.com/polyfy/polylith\"")
   (str "                                 :sha       \"" common/poly-git-sha "\"")
   (str "                                 :deps/root \"environments/poly\"}}}}}")])

(defn create-user-config-if-not-exists []
  (let [home-dir (user-config/home-dir)
        user-config-file (str home-dir "/.polylith/config.edn")]
    (when (-> user-config-file file/exists not)
      (file/create-missing-dirs user-config-file)
      (file/create-file user-config-file (user-config-content)))))

(defn create-ws [ws-dir ws-name top-ns]
  (file/create-dir ws-dir)
  (file/create-dir (str ws-dir "/bases"))
  (file/create-dir (str ws-dir "/components"))
  (file/create-dir (str ws-dir "/development"))
  (file/create-dir (str ws-dir "/development/src"))
  (file/create-dir (str ws-dir "/environments"))
  (file/create-file (str ws-dir "/.gitignore") gitignore-content)
  (file/create-file (str ws-dir "/deps.edn") (deps-content top-ns))
  (file/create-file (str ws-dir "/readme.md") (readme-content ws-name))
  (file/create-file (str ws-dir "/development/src/.keep") [""])
  (file/copy-resource-file! "creator/logo.png" (str ws-dir "/logo.png"))
  (create-user-config-if-not-exists)
  (git/init ws-dir))

(defn create [root-dir ws-name top-ns]
  (let [ws-dir (str root-dir "/" ws-name)]
    (if (file/exists ws-dir)
      (println (str "  Workspace '" ws-name "' already exists."))
      (create-ws ws-dir ws-name top-ns))))
