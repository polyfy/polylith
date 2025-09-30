(ns ^:no-doc polylith.clj.core.creator.workspace
  (:require [clojure.string :as str]
            [polylith.clj.core.creator.shared :as shared]
            [polylith.clj.core.creator.template :as template]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.version.interface :as version]))

(defn- gitignore-content [ws-dir data]
  [(template/render ws-dir ".gitignore" data)])

(defn readme-content [ws-dir data]
  [(template/render ws-dir "readme.md" data)])

(defn workspace-content [ws-dir data]
  [(template/render ws-dir "workspace.edn" data)])

(defn mvn-version []
  version/name)

(defn deps-content [ws-dir data]
  [(template/render ws-dir "deps.edn" data)])

(defn npm-content [ws-dir data]
  [(template/render ws-dir "package.json" data)])

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

(defn create-ws [ws-dir ws-name top-ns template-data ws-dialects create-ws-dir? git-repo? branch commit?]
  (let [data (merge {:clojure-ver shared/clojure-ver
                     :maven-ver (mvn-version)
                     :shadow-cljs-ver shared/shadow-cljs-ver
                     :top-ns top-ns
                     :ws-name ws-name
                     :ws-dialects ws-dialects}
                    template-data)]
    (when create-ws-dir?
      (file/create-dir ws-dir))
    (file/create-dir (str ws-dir "/bases"))
    (file/create-dir (str ws-dir "/components"))
    (file/create-dir (str ws-dir "/development"))
    (file/create-dir (str ws-dir "/development/src"))
    (file/create-dir (str ws-dir "/projects"))
    (file/create-dir (str ws-dir "/.vscode"))
    (file/create-file-if-not-exists (str ws-dir "/.gitignore") (gitignore-content ws-dir data))
    (file/create-file (str ws-dir "/workspace.edn") (workspace-content ws-dir data))
    (file/create-file (str ws-dir "/deps.edn") (deps-content ws-dir data))
    (when (contains? (set ws-dialects) "cljs")
      (file/create-file (str ws-dir "/package.json") (npm-content ws-dir data)))
    (file/create-file (str ws-dir "/readme.md") (readme-content ws-dir data))
    (file/create-file-if-not-exists (str ws-dir "/.vscode/settings.json") (calva-settings-content ws-name))
    (file/create-file (str ws-dir "/development/src/.keep") [""])
    (file/create-file (str ws-dir "/components/.keep") [""])
    (file/create-file (str ws-dir "/bases/.keep") [""])
    (file/create-file (str ws-dir "/projects/.keep") [""])
    (file/copy-resource-file! "creator/logo.png" (str ws-dir "/logo.png")))
  (when commit?
    (git/init ws-dir git-repo? branch))
  (when git-repo?
    (println "  Workspace created in existing git repo.")))

(defn create [workspace root-dir ws-name top-ns ws-dialects branch commit?]
  (let [create-ws-dir? (not (str/blank? ws-name))
        ws-dir (if create-ws-dir? (str root-dir "/" ws-name) root-dir)
        git-repo? (git/is-git-repo? root-dir)
        template-data (-> workspace :configs :workspace :template-data)]
    (cond
      (and create-ws-dir?
           (file/exists ws-dir)) (println (str "  Workspace '" ws-name "' already exists."))
      (and (not create-ws-dir?)
           (not git-repo?)) (println "  Current directory must be a git repo. Leave out :commit and try again.")
      (and commit? git-repo?) (println "  Can't commit a workspace inside an existing git repo.")
      :else (create-ws ws-dir ws-name top-ns template-data ws-dialects create-ws-dir? git-repo? branch commit?))))
