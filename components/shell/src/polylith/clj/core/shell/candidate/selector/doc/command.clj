(ns ^:no-doc polylith.clj.core.shell.candidate.selector.doc.command
  (:require [clojure.string :as str]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.shell.candidate.selector.doc.shared :as shared]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]))

(defn command [bookmark]
  (let [the-rest (subs bookmark 2)
        cmd (subs the-rest 0 (-> the-rest count dec))]
    [cmd []]))

(comment
  ;; commands
  (into (sorted-map)
        (mapv command
              (filter #(str/starts-with? % "[#")
                      (-> "doc/commands.adoc"
                          slurp
                          str/split-lines))))
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

(defn select [_ groups _]
  (let [current (or (get-in groups [:doc "command" :args]) [])
        values (ws-explorer/extract commands current)
        result (shared/strings values (ws-explorer/extract commands (conj current "keys")))]
    (mapv #(c/fn-comma-arg % :doc "command" #'select true)
          result)))
