(ns polylith.clj.core.api.interface
  "If we want to build tooling around Polylith or e.g. use `poly` functionality in our
   `build.clj` file, then we can use the [clj-poly](https://clojars.org/polylith/clj-poly) library
   and get access to some of the functionality that is included in the `poly` command line tool.

   The difference between the library and the `poly` tool is that the latter is AOT compiled
   into Java bytecode, while `clj-poly` is just a normal Clojure library (source code that is zipped
   into a jar file). This is good, because now we can expect the same behaviour from both.

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
   ```

   If you need more access than is provided by the api at the moment, just reach out to the
   Polylith team in [Slack](https://clojurians.slack.com/messages/C013B7MQHJQ).

   All other code that is not part of the public API, is used at your own risk, and may change
   in a way that may break your code, when bumping the `clj-poly` version."
  (:require [polylith.clj.core.api.core :as core]
            [polylith.clj.core.version.interface :as version])
  (:gen-class))

(def api-version
  "The version of the different types of APIs, stored in a hash map with these keys:
  ```clojure
   :api           The version of this API.
   :test-runner   The version of the test runner.
   :ws            The version of the workspace structure.
  ```

  Each key stores a map with two keys:
  ```clojure
   :breaking       Increased by one if a breaking change + set `:non-breaking` to zero.
   :non-breaking   Increased by one if a non-breaking change.
  ```

  When a new version of [clj-poly](https://clojars.org/polylith/clj-poly) is released to Clojars,
  any of the three APIs may change version.

  Examples of a breaking change of the workspace structure (`:ws`):
  ```
  - If an attribute changes name.
  - If an attribute is deleted.
  - If an attribute starts to store its data in a breaking way.
  ```
  Any changes that only add functionality/attributes, will increase the `:non-breaking` number by one.

  The ambition is to  never break `:api` and `:test-runner`. With `:ws` it's different, and we know
  that the workspace structure will change over time, so pay extra attention every time
  you bump `clj-poly` and have a look in the `version` component's interface, that lists all the
  changes for different versions.

  If we use a SNAPSHOT version, then we can check [next-release.txt](https://github.com/polyfy/polylith/blob/issue-318/next-release.txt)
  to get a summary of all the changes that have been made since the last final release."
  {:api version/api-version
   :test-runner version/test-runner-api-version
   :ws version/ws-api-version})

(defn projects-to-deploy
  "This function returns the projects that have changed (directly or indirectly) since the
  _last stable point in time_, and is primarily used to know which projects we should build and deploy.

  If called with:
  ```clojure
  (projects-to-deploy \"release\")
  ```

  ...it will execute the equivalent to this under the hood:

  ```
  poly ws get:changes:changed-or-affected-projects since:release skip:development
  ```

  ...which returns all changed or affected projects, except `development`, since the last release.
  If `since` is passed in as `nil` then `stable` will be used."
  [since]
  (core/projects-to-deploy since))

(defn workspace
  "Returns the workspace or part of the workspace by passing in either
   a key that can be found in the `:tag-patterns` keys in `workspace.edn`,
   optionally prefixed with `previous-`, or a git SHA, as the first argument,
   and a list of keywords, strings, or numbers as the second argument.
   `:keys` and `:count` are also valid keys to pass in.

   Examples
   ```clojure
   ;; Returns the whole workspace structure, equivalent to: poly ws since:stable
   (workspace nil)

   ;; Returns the whole workspace structure, equivalent to: poly ws since:release
   (workspace \"release\")

   ;; Returns the interface namespace name, equivalent to: poly ws get:settings:interface-ns
   (workspace nil \"settings\" \"interface-ns\")
   ```

   Passing in `since` is only needed if we access things under the `:changes` top key.

   Avoid using things under `:configs`, if they can be found in e.g. `:settings`,
   `:components`, `:bases`, `:projects`, or elsewhere.

   > Since version `0.2.18` we only publish `clj-poly` to Clojars and not the old `clj-api`."
  [stable-point & keys]
  (core/workspace stable-point keys))

(comment
  (projects-to-deploy nil)
  (projects-to-deploy "release")
  (workspace nil)
  (workspace "release")
  (workspace "stable" "settings" "interface-ns")
  #__)
