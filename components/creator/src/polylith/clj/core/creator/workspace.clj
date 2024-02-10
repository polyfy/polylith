(ns ^:no-doc polylith.clj.core.creator.workspace
  (:require [clojure.string :as str]
            [polylith.clj.core.creator.shared :as shared]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.version.interface :as version]))

(def gitignore-content
  ["**/classes"
   "**/target"
   "**/.artifacts"
   "**/.cpcache"
   "**/.DS_Store"
   "**/.gradle"
   ""
   "# User-specific stuff"
   ".idea/**/workspace.xml"
   ".idea/**/tasks.xml"
   ".idea/**/usage.statistics.xml"
   ".idea/**/shelf"
   ".idea/**/statistic.xml"
   ".idea/dictionaries/**"
   ".idea/libraries/**"
   ""
   "# File-based project format"
   "*.iws"
   "*.ipr"
   ""
   "# Cursive Clojure plugin"
   ".idea/replstate.xml"
   "*.iml"
   ""
   "/example/example/**"
   "artifacts"
   "projects/**/pom.xml"
   ""
   "# nrepl"
   ".nrepl-port"
   ""
   "# clojure-lsp"
   ".lsp/.cache"
   ""
   "# clj-kondo"
   ".clj-kondo/.cache"
   ""
   "# Calva VS Code Extension"
   ".calva/output-window/output.calva-repl"])

(defn readme-content [ws-name]
  ["<img src=\"logo.png\" width=\"30%\" alt=\"Polylith\" id=\"logo\">"
   ""
   "The Polylith documentation can be found here:"
   ""
   "- The [high-level documentation](https://polylith.gitbook.io/polylith)"
   "- The [poly tool documentation](https://cljdoc.org/d/polylith/clj-poly/CURRENT)"
   "- The [RealWorld example app documentation](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app)"
   ""
   "You can also get in touch with the Polylith Team on [Slack](https://clojurians.slack.com/archives/C013B7MQHJQ)."
   ""
   (str "<h1>" ws-name "</h1>")
   ""
   "<p>Add your workspace documentation here...</p>"])

(defn workspace-content [top-ns]
  [(str "{:top-namespace \"" top-ns "\"")
   (str " :interface-ns \"interface\"")
   (str " :default-profile-name \"default\"")
   (str " :compact-views #{}")
   (str " :vcs {:name \"git\"")
   (str "       :auto-add false}")
   (str " :tag-patterns {:stable \"stable-*\"")
   (str "                :release \"v[0-9]*\"}")
   (str " :projects {\"development\" {:alias \"dev\"}}}")])

(defn mvn-version []
  version/name)

(defn deps-content []
  [(str "{:aliases  {:dev {:extra-paths [\"development/src\"]")
   (str "")
   (str "                  :extra-deps {org.clojure/clojure {:mvn/version \"" shared/clojure-ver "\"}}}")
   (str "")
   (str "            :test {:extra-paths []}")
   (str "")
   (str "            :poly {:main-opts [\"-m\" \"polylith.clj.core.poly-cli.core\"]")
   (str "                   :extra-deps {polylith/clj-poly {:mvn/version \"" (mvn-version) "\"}}}}}")])

(defn calva-settings-content [ws-name]
  [(str "{")
   (str "    \"calva.replConnectSequences\": [")
   (str "        {")
   (str "            \"projectType\": \"deps.edn\",")
   (str "            \"name\": \"" ws-name "\",")
   (str "            \"cljsType\": \"none\",")
   (str "            \"menuSelections\": {")
   (str "                \"cljAliases\": [\"dev\", \"test\", \"+default\"]")
   (str "            }")
   (str "        }")
   (str "    ]")
   (str "}")])

(defn create-ws [ws-dir ws-name top-ns create-ws-dir? git-repo? branch commit?]
  (when create-ws-dir?
    (file/create-dir ws-dir))
  (file/create-dir (str ws-dir "/bases"))
  (file/create-dir (str ws-dir "/components"))
  (file/create-dir (str ws-dir "/development"))
  (file/create-dir (str ws-dir "/development/src"))
  (file/create-dir (str ws-dir "/projects"))
  (file/create-dir (str ws-dir "/.vscode"))
  (file/create-file-if-not-exists (str ws-dir "/.gitignore") gitignore-content)
  (file/create-file (str ws-dir "/workspace.edn") (workspace-content top-ns))
  (file/create-file (str ws-dir "/deps.edn") (deps-content))
  (file/create-file (str ws-dir "/readme.md") (readme-content ws-name))
  (file/create-file-if-not-exists (str ws-dir "/.vscode/settings.json") (calva-settings-content ws-name))
  (file/create-file (str ws-dir "/development/src/.keep") [""])
  (file/create-file (str ws-dir "/components/.keep") [""])
  (file/create-file (str ws-dir "/bases/.keep") [""])
  (file/create-file (str ws-dir "/projects/.keep") [""])
  (file/copy-resource-file! "creator/logo.png" (str ws-dir "/logo.png"))
  (when commit?
    (git/init ws-dir git-repo? branch))
  (when git-repo?
    (println "  Workspace created in existing git repo.")))

(defn create [root-dir ws-name top-ns branch commit?]
  (let [create-ws-dir? (not (str/blank? ws-name))
        ws-dir (if create-ws-dir? (str root-dir "/" ws-name) root-dir)
        git-repo? (git/is-git-repo? root-dir)]
    (cond
      (and create-ws-dir?
           (file/exists ws-dir)) (println (str "  Workspace '" ws-name "' already exists."))
      (and (not create-ws-dir?)
           (not git-repo?)) (println "  Current directory must be a git repo. Leave out :commit and try again.")
      (and commit? git-repo?) (println "  Can't commit a workspace inside an existing git repo.")
      :else (create-ws ws-dir ws-name top-ns create-ws-dir? git-repo? branch commit?))))
