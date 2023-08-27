(ns polylith.clj.core.api.interface
  "If we want to build tooling around Polylith or e.g. use `poly` functionality in our
   `build.clj` file, then we can use `poly` as a library.

   The `poly` tool and the [clj-poly](https://clojars.org/polylith/clj-poly) library
   contain the exact same code with the same set of bricks, at least if we use version
   `0.2.18` or later.

   The only difference is that the `poly` tool is AOT compiled into Java bytecode, while
   `clj-poly` is a normal Clojure library, source code that is zipped into a jar file.
   This is good, because now we can expect the same behaviour from both.

   We can use the `poly` tool as a library by including the `clj-poly` library
   as a dependency to a project's `deps.edn` file, e.g.:
   ```clojure
   :aliases {:dostuff {...
                       :extra-deps {...
                                    polylith/clj-poly {:mvn/version \"0.2.18\"}}
   ```

   ...or by selecting a [sha](https://github.com/polyfy/polylith/commits/master) from
   the polylith GitHub repo, e.g.:
   ```clojure
   ...
   polylith/clj-poly {:git/url   \"https://github.com/polyfy/polylith.git\"
                      :sha       \"3b3e4ceac31d27f9a87862286f1ba01a3e4705ef\"
                      :deps/root \"projects/poly\"}
   ```"
  (:require [polylith.clj.core.api.core :as core]
            [polylith.clj.core.version.interface :as version])
  (:gen-class))

(def api-version
  "The version of the different types of APIs, stored in a hash map with these keys:
  ```clojure
  `:api`          The version of this API.
  `:test-runner`  The version of the test runner.
  `:ws`           The version of the workspace structure.

  Each key stores a map with two keys:
  ```clojure
  `:breaking`      Increased by one if a breaking change + `:non-breaking` is set to 0.
  `:non-breaking`  Increased by one if non-breaking change.
  ```"
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
