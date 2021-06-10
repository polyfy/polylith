(ns polylith.clj.core.creator.workspace
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.git.interface :as git]))

(def gitignore-content
  ["**/classes"
   "**/target"])

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

(defn workspace-content [top-ns is-git-add]
  [(str "{:top-namespace \"" top-ns "\"")
   (str " :interface-ns \"interface\"")
   (str " :default-profile-name \"default\"")
   (str " :compact-views #{}")
   (str " :vcs {:name \"git\"")
   (str "       :auto-add " (or is-git-add false) "}")
   (str " :tag-patterns {:stable \"stable-*\"")
   (str "                :release \"v[0-9]*\"}")
   (str " :projects {\"development\" {:alias \"dev\"}}}")])

(defn latest-sha [branch]
  (if-let [sha (git/latest-polylith-sha (or branch git/branch))]
    [false sha]
    [true "INSERT_LATEST_SHA_HERE"]))

(defn deps-content [sha]
  [(str "{:aliases  {:dev {:extra-paths [\"development/src\"]")
   (str "                  :extra-deps {org.clojure/clojure {:mvn/version \"1.10.1\"}")
   (str "                               org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}}")
   (str "")
   (str "            :test {:extra-paths []}")
   (str "")
   (str "            :poly {:main-opts [\"-m\" \"polylith.clj.core.poly-cli.core\"]")
   (str "                   :extra-deps {polyfy/polylith")
   (str "                                {:git/url   \"https://github.com/polyfy/polylith\"")
   (str "                                 :sha       \"" sha "\"")
   (str "                                 :deps/root \"projects/poly\"}}}}}")])

(defn create-ws [ws-dir ws-name top-ns create-ws-dir? git-repo? insert-sha? sha branch is-git-add]
  (when create-ws-dir?
    (file/create-dir ws-dir))
  (file/create-dir (str ws-dir "/bases"))
  (file/create-dir (str ws-dir "/components"))
  (file/create-dir (str ws-dir "/development"))
  (file/create-dir (str ws-dir "/development/src"))
  (file/create-dir (str ws-dir "/projects"))
  (file/create-file (str ws-dir "/.gitignore") gitignore-content)
  (file/create-file (str ws-dir "/workspace.edn") (workspace-content top-ns is-git-add))
  (file/create-file (str ws-dir "/deps.edn") (deps-content sha))
  (file/create-file (str ws-dir "/readme.md") (readme-content ws-name))
  (file/create-file (str ws-dir "/development/src/.keep") [""])
  (file/create-file (str ws-dir "/components/.keep") [""])
  (file/create-file (str ws-dir "/bases/.keep") [""])
  (file/create-file (str ws-dir "/projects/.keep") [""])
  (file/copy-resource-file! "creator/logo.png" (str ws-dir "/logo.png"))
  (git/init ws-dir git-repo? branch)
  (when git-repo?
    (println "  Workspace created in existing git repo."))
  (when insert-sha?
    (println (str "  Make sure to replace INSERT_LATEST_SHA_HERE in './deps.edn' with the latest SHA "
                  "from https://github.com/polyfy/polylith/commits/" (git/current-branch) "."))))

(defn create [root-dir ws-name top-ns branch is-git-add]
  (let [create-ws-dir? (not (str/blank? ws-name))
        ws-dir (if create-ws-dir? (str root-dir "/" ws-name) root-dir)
        [insert-sha? sha] (latest-sha branch)
        git-repo? (git/is-git-repo? root-dir)]
    (cond
      (and create-ws-dir?
           (file/exists ws-dir)) (println (str "  Workspace '" ws-name "' already exists."))
      (and (not create-ws-dir?)
           (not git-repo?)) (println "  Current directory must be a git repo.")
      :else (create-ws ws-dir ws-name top-ns create-ws-dir? git-repo? insert-sha? sha branch is-git-add))))
