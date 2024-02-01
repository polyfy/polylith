(ns polylith.clj.core.api.interface
  "If we want to build tooling around Polylith or e.g. use `poly` functionality in our
   `build.clj` file, then we can use the [clj-poly](https://clojars.org/polylith/clj-poly) library
   and get access to some of the functionality that is included in the `poly` command line tool.

   The difference between the `clj-poly` library and the `poly` tool is that the latter is AOT compiled
   into Java bytecode, while the library is just a normal Clojure library (source code that is zipped
   into a jar file). This is good, because now we can expect the same behaviour from both.

   We can use the `poly` tool as a library by including the `clj-poly` library
   as a dependency to a project's `deps.edn` file, e.g.:
   ```clojure
   :aliases {:dostuff {...
                       :extra-deps {...
                                    polylith/clj-poly {:mvn/version \"0.2.19\"}}
   ```

   ...or by selecting a [sha](https://github.com/polyfy/polylith/commits/master) from
   the polylith GitHub repo, e.g.:
   ```clojure
   ...
   polylith/clj-poly {:git/url   \"https://github.com/polyfy/polylith.git\"
                      :sha       \"3b3e4ceac31d27f9a87862286f1ba01a3e4705ef\"
                      :deps/root \"projects/poly\"}
   ```

   As an example, the `clj-poly` library is first specified in the
   [build](https://github.com/polyfy/polylith/blob/5ff79341e7dc3fc6a486584c6c2f2f46cb577d6e/deps.edn#L120)
   alias in polylith's `build.clj` and then used in
   [build.clj](https://github.com/polyfy/polylith/blob/9e79264981b0c5be6e6cb70c93a540a82c489510/build.clj#L83).

   If you need more access than is exposed by the API at the moment, just reach out to the
   Polylith team in [Slack](https://clojurians.slack.com/messages/C013B7MQHJQ)
   and we will try to help out.

   All other code that is not part of the public API, is used at your own risk,
   and may change in a breaking way between `clj-poly` versions."
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
   :breaking       Increased by one if a breaking change + :non-breaking is set to zero.
   :non-breaking   Increased by one if a non-breaking change.
  ```

  When a new version of the [clj-poly](https://clojars.org/polylith/clj-poly) library is released to Clojars,
  any of the three APIs may change version, but the ambition is to never break `:api` and `:test-runner`.

  With `:ws` it's different, and we know that the workspace structure will change over time, so pay extra
  attention every time you bump `clj-poly` and have a look at the [versions](https://cljdoc.org/d/polylith/clj-poly/CURRENT/doc/versions) page,
  which lists all the changes for different versions.

  Examples of a breaking change of the workspace structure:
  ```
  - If an attribute changes name.
  - If an attribute is deleted.
  - If an attribute starts to store its data in a breaking way.
  ```
  Any changes that only add functionality/attributes, will increase the `:non-breaking` number by one.

  If you use a SNAPSHOT version, then you can check [next-release.txt](https://github.com/polyfy/polylith/blob/master/next-release.txt)
  to get a summary of all the changes that have been made since the last stable/final release."
  {:api version/api-version
   :test-runner version/test-runner-api-version
   :ws version/workspace-version})

(defn check
  "Returns true if no error messages + a vector of error messages.
   ```clojure
   {:ok? true
    :error-messages []}
   ```"
  [since]
  (core/check since))

(defn projects-to-deploy
  "Returns the projects that have changed (directly or indirectly) since the _last stable point in time_,
   and is primarily used to know which projects to build and deploy.

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
   a key that can be found in the `:tag-patterns` keys in `workspace.edn`
   optionally prefixed with `previous-`, or a git SHA, as the first argument,
   and a list of keywords, strings, or numbers as the second argument.
   `:keys` and `:count` are also valid keys to pass in.

   Examples:
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

   Note that since version `0.2.18` we only publish `clj-poly` to Clojars and not the old `clj-api`."
  [since & keys]
  (core/workspace since keys))

(comment
  (projects-to-deploy nil)
  (projects-to-deploy "release")
  (workspace nil)
  (workspace "release")
  (workspace "stable" "settings" "interface-ns")
  #__)
