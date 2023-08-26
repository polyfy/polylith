(ns polylith.clj.core.api.interface
  "This namespace contains the `poly` tool functionality that is exposed as a library.
   Read more in the `poly` tool documentation, by executing:
   ```clojure
     poly doc page:poly-as-a-library
   ```"
  (:require [polylith.clj.core.api.core :as core]
            [polylith.clj.core.version.interface :as version])
  (:gen-class))

(def api-version
  "The version of the different types of APIs, stored in a hash map with the keys:
   `:api`, `:test-runner`, and `:ws`."
  {:api version/api-version
   :test-runner version/test-runner-api-version
   :ws version/ws-api-version})

(defn projects-to-deploy
  "Returns the projects that have been affected since last deploy,
   tagged in git following the pattern defined by `:tag-patterns` in
   `./deps.edn`, or `v[0-9]*` if not specified."
  [since]
  (core/projects-to-deploy since))

(defn workspace
  "Returns the workspace or part of the workspace by sending in either
   a key that can be found in the `:tag-patterns` keys in `workspace.edn`,
   optionally prefixed with `previous-`, or a git SHA, as the first argument,
   and a list of keywords, strings, or numbers as the second argument.
   `:keys` and `:count` are also valid keys to pass in.

   Examples
   ```clojure
   ;; Returns the whole workspace structure, same as: poly ws since:stable
   (workspace nil)

   ;; Returns the whole workspace structure, same as: poly ws since:release
   (workspace \"release\")

   ;; Returns the interface namespace name, same as: poly ws get:settings:interface-ns
   (workspace nil \"settings\" \"interface-ns\")
   ```"
  [stable-point & keys]
  (core/workspace stable-point keys))

(comment
  (projects-to-deploy nil)
  (projects-to-deploy "release")
  (workspace nil)
  (workspace "release")
  (workspace "stable" "settings" "interface-ns")
  #__)
