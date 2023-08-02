(ns ^:no-doc polylith.clj.core.shell.candidate.selector.doc-sections
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.config-reader.interface :as config-reader]
            [polylith.clj.core.shell.candidate.shared :as shared]
;            [polylith.clj.core.ws-explorer.interface :as ws-explorer]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn page [item]
  {:name (-> item
             first
             str/lower-case
             (str/replace #" " "_"))
   :page (-> item
             second
             :file
             (str-util/skip-prefix "doc/"))})

(defn subpage [header item]
  (let [{:keys [_ page]} (page item)]
    {:name (str-util/skip-suffix page ".adoc")
     :page (str header "/" page)}))

(defn pages [menu-item]
  (let [{:keys [name page] :as item} (page menu-item)]
    (if page
      [item]
      (mapv #(subpage name %)
            (drop 2 menu-item)))))

(comment
  (def cljdoc-pages (-> (config-reader/read-edn-file "doc/cljdoc.edn" "cljdoc.edn")
                        :config :cljdoc.doc/tree))

  ;; Extract the pages in the documentation
  (vec (sort-by :name (mapcat pages cljdoc-pages)))
  #__)

(def cljdoc-pages [{:name "base", :page "base.adoc"}
                   {:name "build", :page "build.adoc"}
                   {:name "clojure_cli_tool", :page "clojure-cli-tool.adoc"}
                   {:name "colors", :page "colors.adoc"}
                   {:name "commands", :page "reference/commands.adoc"}
                   {:name "component", :page "component.adoc"}
                   {:name "configuration", :page "configuration.adoc"}
                   {:name "context", :page "context.adoc"}
                   {:name "continuous_integration", :page "continuous-integration.adoc"}
                   {:name "dependencies", :page "dependencies.adoc"}
                   {:name "development", :page "development.adoc"}
                   {:name "example_systems", :page "example-systems.adoc"}
                   {:name "explore_the_workspace", :page "explore-the-workspace.adoc"}
                   {:name "flags", :page "flags.adoc"}
                   {:name "git", :page "git.adoc"}
                   {:name "git_hooks", :page "git-hooks.adoc"}
                   {:name "install", :page "install.adoc"}
                   {:name "interface", :page "interface.adoc"}
                   {:name "introduction", :page "introduction.adoc"}
                   {:name "libraries", :page "libraries.adoc"}
                   {:name "migrate", :page "migrate.adoc"}
                   {:name "naming", :page "naming.adoc"}
                   {:name "parameters", :page "parameters.adoc"}
                   {:name "polyx", :page "polyx.adoc"}
                   {:name "profile", :page "profile.adoc"}
                   {:name "project", :page "project.adoc"}
                   {:name "readme", :page "readme.adoc"}
                   {:name "shell", :page "shell.adoc"}
                   {:name "source_code", :page "source-code.adoc"}
                   {:name "tagging", :page "tagging.adoc"}
                   {:name "tap", :page "tap.adoc"}
                   {:name "test_runners", :page "test-runners.adoc"}
                   {:name "testing", :page "testing.adoc"}
                   {:name "tools.deps", :page "tools-deps.adoc"}
                   {:name "upgrade", :page "upgrade.adoc"}
                   {:name "validations", :page "validations.adoc"}
                   {:name "workspace", :page "workspace.adoc"}
                   {:name "workspace-structure", :page "reference/workspace-structure.adoc"}] )

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

;(defn select [_ groups workspace]
;  (let [current (or (get-in groups [:doc "poly" :args]) [])
;        values (ws-explorer/extract workspace current)
;        result (strings values (ws-explorer/extract workspace (conj current "keys")))]
;    (mapv #(c/fn-comma-arg % :doc "poly" #'select true)
;          result)))


(defn select [{:keys [group]} groups {:keys [settings]}]
  (let [color-mode (:color-mode settings)]
    (mapv #(c/fn-explorer-child % :doc color-mode false group #'select)
          (sort (set/difference
                  (set (map :name cljdoc-pages))
                  (set (shared/args groups group)))))))
