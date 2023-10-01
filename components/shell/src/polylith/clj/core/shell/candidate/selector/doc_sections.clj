(ns ^:no-doc polylith.clj.core.shell.candidate.selector.doc-sections
  (:require [clojure.string :as str]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.config-reader.interface :as config-reader]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn page-name [item]
  (-> item
      second
      :file
      (str-util/skip-prefix "doc/")
      (str-util/skip-suffix ".adoc")))

(defn pages-info [item]
  (let [page (page-name item)]
    (if page
      [page]
      (mapv page-name
            (drop 2 item)))))

(defn command [bookmark]
  (let [the-rest (subs bookmark 2)
        cmd (subs the-rest 0 (-> the-rest count dec))]
    [cmd []]))

(defn ws-key [bookmark]
  [(subs bookmark 3) []])

(defn entry [page]
  [page {}])

(comment
  ;; commands
  (into (sorted-map)
        (mapv command
              (filter #(str/starts-with? % "[#")
                      (-> "doc/commands.adoc"
                          slurp
                          str/split-lines))))

  ;; ws-structure
  (into (sorted-map)
        (mapv ws-key
              (filter #(str/starts-with? % "== ")
                      (-> "doc/workspace-structure.adoc"
                          slurp
                          str/split-lines))))

  (def cljdoc-pages (-> (config-reader/read-edn-file "doc/cljdoc.edn" "cljdoc.edn")
                        :config :cljdoc.doc/tree))
  ;; pages
  (into (sorted-map) (mapv entry (sort (mapcat pages-info cljdoc-pages))))



  #__)

(def commands {"check" [],
               "create" [],
               "create-base" [],
               "create-component" [],
               "create-project" [],
               "create-workspace" [],
               "deps" [],
               "deps-brick" [],
               "deps-project" [],
               "deps-project-brick" [],
               "deps-workspace" [],
               "diff" [],
               "info" [],
               "libs" [],
               "migrate" [],
               "overview" [],
               "shell" [],
               "switch-ws" [],
               "tap" [],
               "test" [],
               "ws" []})

(def pages {"base" {},
            "build" {},
            "clojure-cli-tool" {},
            "colors" {},
            "commands" {},
            "component" {},
            "configuration" {},
            "context" {},
            "continuous-integration" {},
            "dependencies" {},
            "development" {},
            "example-systems" {},
            "explore-the-workspace" {},
            "flags" {},
            "git" {},
            "git-hooks" {},
            "install" {},
            "interface" {},
            "introduction" {},
            "libraries" {},
            "migrate" {},
            "naming" {},
            "parameters" {},
            "polyx" {},
            "profile" {},
            "project" {},
            "readme" {},
            "shell" {},
            "source-code" {},
            "tagging" {},
            "tap" {},
            "test-runners" {},
            "testing" {},
            "tools-deps" {},
            "upgrade" {},
            "validations" {},
            "workspace" {},
            "ws-structure" {}})

(def ws-structure {"bases" [],
                   "changes" [],
                   "components" [],
                   "configs" [],
                   "interface" [],
                   "messages" [],
                   "name" [],
                   "old" [],
                   "paths" [],
                   "projects" [],
                   "settings" [],
                   "user-input" [],
                   "version" [],
                   "ws-dir" [],
                   "ws-local-dir" [],
                   "ws-reader" [],
                   "ws-type" []})

(defn map-strings [values]
  (if (-> values first keyword?)
    (map name values)
    values))

(defn seq-strings [values raw-values]
  (if (-> raw-values first string?)
    []
    values))

(defn strings [raw-values values]
  (cond (map? raw-values) (map-strings values)
        (sequential? raw-values) (seq-strings values raw-values)
        :else values))

(defn select-command [_ groups _]
  (let [current (or (get-in groups [:doc "command" :args]) [])
        values (ws-explorer/extract commands current)
        result (strings values (ws-explorer/extract commands (conj current "keys")))]
    (mapv #(c/fn-comma-arg % :doc "command" #'select-command true)
          result)))

(defn select-page [_ groups _]
  (let [current (or (get-in groups [:doc "page" :args]) [])
        values (ws-explorer/extract pages current)
        result (strings values (ws-explorer/extract pages (conj current "keys")))]
    (mapv #(c/fn-comma-arg % :doc "page" #'select-page true)
          result)))

(defn select-ws [_ groups _]
  (let [current (or (get-in groups [:doc "ws" :args]) [])
        values (ws-explorer/extract ws-structure current)
        result (strings values (ws-explorer/extract ws-structure (conj current "keys")))]
    (mapv #(c/fn-comma-arg % :doc "ws" #'select-ws true)
          result)))
