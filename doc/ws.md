
# The workspace structure

The `poly` tool gives us access to the data structure that represents the workspace,
which is the same structure that all the `poly` commands operate on.

Here we will use the output from the [example in the documentation](examples/doc-example).

To list all the keys at the root, execute `poly ws get:keys` from the `examples/doc-example` directory:
```
[:bases
 :components
 :changes
 :interfaces
 :messages
 :name
 :old
 :paths
 :projects
 :settings
 :user-input
 :version
 :ws-dir
 :ws-local-dir
 :ws-reader]
```

Let's go through all the keys.

## bases

`poly ws get:bases:user-api`

```
{:interface-deps {:src ["user"], :test []},
 :lib-deps {:src {"slacker/slacker" {:size 28408,
                                     :type "maven",
                                     :version "0.17.0"}}},
 :lib-imports {:src ["se.example.user-api.api" "slacker.server"],
               :test ["clojure.test" "se.example.user-api.core"]},
 :lines-of-code {:src 13, :test 6},
 :name "user-api",
 :namespaces {:src [{:file-path "/Users/joakimtengstrand/source/polylith/examples/doc-example/bases/user-api/src/se/example/user_api/core.clj",
                     :imports ["se.example.user-api.api" "slacker.server"],
                     :name "core",
                     :namespace "se.example.user-api.core"}
                    {:file-path "/Users/joakimtengstrand/source/polylith/examples/doc-example/bases/user-api/src/se/example/user_api/api.clj",
                     :imports ["se.example.user.interface"],
                     :name "api",
                     :namespace "se.example.user-api.api"}],
              :test [{:file-path "/Users/joakimtengstrand/source/polylith/examples/doc-example/bases/user-api/test/se/example/user_api/core_test.clj",
                      :imports ["clojure.test" "se.example.user-api.core"],
                      :name "core-test",
                      :namespace "se.example.user-api.core-test"}]},
 :paths {:src ["src" "resources"], :test ["test"]},
 :type "base"}
```

- `:interface-deps` Lists the interfaces each base depens on. Here it depends on the `user` interface from the `src` context.
  This is because the `se.example.user-api.api` namespace imports `se.example.user.interface`.
  No interfaces are imported from the `test` context in this example.
- `:lib-deps` Lists the library dependencies the base depends on, defined in the `:deps` key in its `deps.edn` file
  for the `src` context, and the `:aliases > :test > :extra-deps` for the `test` context. The `:size` is in bytes.
- `:lib-imports` The name is a bit misleading as internal namespaces for the base is also included.
  - `src` Lists all imports from the `src` context except component interfaces.
  - `test` Lists all imports from the `test` context except component interfaces.
- `:lines-of-code` Total number of lines of code for this base (all namespaces included).
- `:name` Then name of the directory for this base under the `bases` directory.
- `:namespaces`
  - `:src` Lists all the namespaces that live under the `src` directory.
    - `:file-path` The absolute path to the namespace.
    - `:imports` Lists all its namespaces.
    - `:name` The name of the namespace where top namespace + base name (`se.example` + `user-api` in thie case) are skipped away.
    - `namespace` The full name of the namespace.
  - `:test` Lists all the namespaces that live under the `test` directory.
- `:paths` The paths that are specified in its `deps.edn` file (`paths` for `src` and `resources` paths + `aliases > :test > :extra-paths` for `test` paths).
- `:type` Set to "base".

## components

`poly ws get:components:user`

```
{:interface {:definitions [{:name "hello",
                            :parameters [{:name "name"}],
                            :type "function"}],
             :name "user"},
 :interface-deps {:src [], :test []},
 :lib-deps {},
 :lib-imports {:test ["clojure.test"]},
 :lines-of-code {:src 9, :test 7},
 :name "user",
 :namespaces {:src [{:file-path "/Users/joakimtengstrand/source/polylith/examples/doc-example/components/user/src/se/example/user/interface.clj",
                     :imports ["se.example.user.core"],
                     :name "interface",
                     :namespace "se.example.user.interface"}
                    {:file-path "/Users/joakimtengstrand/source/polylith/examples/doc-example/components/user/src/se/example/user/core.clj",
                     :imports [],
                     :name "core",
                     :namespace "se.example.user.core"}],
              :test [{:file-path "/Users/joakimtengstrand/source/polylith/examples/doc-example/components/user/test/se/example/user/interface_test.clj",
                      :imports ["clojure.test" "se.example.user.interface"],
                      :name "interface-test",
                      :namespace "se.example.user.interface-test"}]},
 :paths {:src ["src" "resources"], :test ["test"]},
 :type "component"}
```
Component keys are the same as for the `base` plus the `:interfaces` key:
- `:interface`
  - `:definitions` Lists all public `def`, `defn` and `defmacro` definitions in the interface namespace. If a type hint is given, 
    then `:type` will also be set and be part of the contract.
- `:interface-deps` Lists the interfaces each component depens on. This component doesn't depend on any other interface.
- `:lib-deps` Lists the library dependencies the component depends on, defined in the `:deps` key in its `deps.edn` file
  for the `src` context, and the `:aliases > :test > :extra-deps` for the `test` context.
- `:lib-imports` Lists all imports except component interfaces. The name is a bit misleading as internal namespaces for the component is also included.
- `:lines-of-code` Total number of lines of code for this compoponent (all namespaces included).
- `:name` Then name of the directory for this component under the `components` directory.
- `:namespaces`
  - `:src` Lists all the namespaces that live under the `src` directory.
    - `:file-path` The absolute path to the namespace.
    - `:imports` Lists all its namespaces.
    - `:name` The name of the namespace where top namespace + component name (`se.example` + `user` in thie case) are skipped away.
    - `namespace` The full name of the namespace.
  - `:test` Lists all the namespaces that live under the `test` directory.
- `:paths` The paths that are specified in its `deps.edn` file (`paths` for `src` and `resources` paths + `aliases > :test > :extra-paths` for `test` paths).
- `:type` Set to "component".

## changes

`poly ws get:changes since:b339c35`

```
{:changed-bases ["cli" "user-api"],
 :changed-components ["user" "user-remote"],
 :changed-files ["bases/cli/deps.edn"
                 "bases/cli/resources/cli/.keep"
                 "bases/cli/src/se/example/cli/core.clj"
                 "bases/cli/test/se/example/cli/core_test.clj"
                 "bases/user-api/deps.edn"
                 "bases/user-api/resources/user-api/.keep"
                 "bases/user-api/src/se/example/user_api/api.clj"
                 "bases/user-api/src/se/example/user_api/core.clj"
                 "bases/user-api/test/se/example/user_api/core_test.clj"
                 "components/user-remote/deps.edn"
                 "components/user-remote/resources/user-remote/.keep"
                 "components/user-remote/src/se/example/user/core.clj"
                 "components/user-remote/src/se/example/user/interface.clj"
                 "components/user-remote/test/se/example/user/interface_test.clj"
                 "components/user/deps.edn"
                 "components/user/resources/user/.keep"
                 "components/user/src/se/example/user/core.clj"
                 "components/user/src/se/example/user/interface.clj"
                 "components/user/test/se/example/user/interface_test.clj"
                 "deps.edn"
                 "development/src/dev/lisa.clj"
                 "projects/command-line/deps.edn"
                 "projects/command-line/test/project/command_line/dummy_test.clj"
                 "projects/user-service/deps.edn"
                 "readme.txt"
                 "scripts/build-cli-uberjar.sh"
                 "scripts/build-uberjar.sh"
                 "scripts/build-user-service-uberjar.sh"
                 "workspace.edn"],
 :changed-or-affected-projects ["command-line" "development" "user-service"],
 :changed-projects ["command-line" "development" "user-service"],
 :git-diff-command "git diff b339c35 --name-only",
 :project-to-bricks-to-test {"command-line" ["cli" "user-remote"],
                             "development" [],
                             "user-service" ["user" "user-api"]},
 :project-to-indirect-changes {"command-line" {:src [], :test []},
                               "development" {:src [], :test []},
                               "user-service" {:src [], :test []}},
 :project-to-projects-to-test {"command-line" [],
                               "development" [],
                               "user-service" []},
 :since "b339c35",
 :since-sha "b339c35"}
```

- `:changed-bases` Lists the changed bases since the sha `b339c35` (or last stable point in time if `:since` is not given).
- `:changed-components` Lists the changed components since the sha `b339c35` (or last stable point in time if `:since` is not given).
- `:changed-files` The same list that is returned by `poly diff since:b339c35`. The keys `:changed-bases`, `:changed-components` and `:changed-projects`
  are calculated from this list.
- `:changed-or-affected-projects` Lists the projects that are directly changed, e.g. its `deps.edn` file, or indirectly changed,
  e.g. if one of the bricks it incudes are changed.
- `:changed-projects` Lists the changed projects since the sha `b339c35` (or last stable point in time if `:since` is not given).
- `:git-diff-command` The git command that was executed to calculate the `:changed-files` list.
- `:project-to-bricks-to-test` A map that stores project names with a list of the bricks to test from that project if executing the `test` command.
- `:project-to-indirect-changes` A map that stores project names with a list of the bricks that are indirectly changed
  (directly changed bricks excluded). E.g. if components `a` and `b` are included in the project, and `a` has not changed,
  but `b` has changed and `a` uses `b`, then `b` will be included in the list.
- `:project-to-projects-to-test` A map that stores project names with a list of projects to test from that project if executing the `test` command.
- `:since` Set to "stable" if `since:SINCE` is not given.
- `:since-sha` The full sha if `since:SINCE` was not given, e.g. `b339c358079fa36ca20ed0163708ba010a0ffd4c`.
- `:since-tag` The name of the tag, e.g. `v0.1.0-alpha9` if `since:release` was given.

## interfaces

`poly ws get:interfaces:user`

```
{:definitions [{:name "hello", 
                :parameters [{:name "name"}], 
                :type "function"}],
 :implementing-components ["user" "user-remote"],
 :name "user",
 :type "interface"}
```

- `:definitions` A list of the public `def`, `defn` and `defmacro` definitions that are part of the interface.
  - `:name` the name of the `def`, `defn` or `defmacro` definition. If it's a multi arity function or macro,
    then each arity will stored separately.
  - `:parameters` Set for functions and macros. Specifies the function/macro parameters:
    - `:name` The name of the parameter.
    - `:type` If a type hint, e.g. `^String` is given then this attribute is set.
  - `:type` Set to `data`, `function` or `macro`.
- `:name` The name of the interface. In this case the bricks `user` and `user-remote` share the same `user`
    interface and live in the `se.example.user.interface` namespace.
- `:type` Set to "interface".

## messages

`poly ws get:messages`

```
[{:code 103,
  :message "Missing definitions in user's interface: hello[name]",
  :colorized-message "Missing definitions in user's interface: hello[name]",
  :components ["user"],
  :type "error"}]
```

To trigger this error, we commented out the `hello` function from the `user` component interface. 

- `:code` The code of the error or warning. To get a full list of existing codes, execute `poly help check`.
- `:message` The error message.
- `:colorized-message` The error message using colors so the text can be printed in color.
- `:components` Each error message can have extra keys/information, like affected components as in this case.
- `type` Set to "error" or "warning".

## name

`poly ws get:name`

```
doc-example
```

The name of the workspace directory.

## old

`poly ws get:old ws-file:ws.edn`

```
{:user-input {:args ["ws" "out:ws.edn"],
              :cmd "ws",
              :is-all false,
              :is-dev false,
              :is-latest-sha false,
              :is-no-exit false,
              :is-run-all-brick-tests false,
              :is-run-project-tests false,
              :is-search-for-ws-dir false,
              :is-show-brick false,
              :is-show-loc false,
              :is-show-project false,
              :is-show-resources false,
              :is-show-workspace false,
              :is-verbose false,
              :out "ws.edn",
              :selected-profiles #{},
              :selected-projects #{},
              :unnamed-args []}}
```

If the workspace is loaded using `ws-file:WS-FILE` then the `:old` key is populated.

- `user-input` The user input from the original `ws` file.
- `:active-profiles` If any profiles are given, then this key is added with the value 
                     of `:active-profiles` taken from the `:settings` key from the 
                     original `ws` file.

## paths

`poly ws get:paths`

```
{:existing ["bases/cli/resources"
            "bases/cli/src"
            "bases/cli/test"
            "bases/user-api/resources"
            "bases/user-api/src"
            "bases/user-api/test"
            "components/user-remote/resources"
            "components/user-remote/src"
            "components/user-remote/test"
            "components/user/resources"
            "components/user/src"
            "components/user/test"
            "development/src"
            "projects/command-line/test"],
 :on-disk ["bases/cli/resources"
           "bases/cli/src"
           "bases/cli/test"
           "bases/user-api/resources"
           "bases/user-api/src"
           "bases/user-api/test"
           "components/user-remote/resources"
           "components/user-remote/src"
           "components/user-remote/test"
           "components/user/resources"
           "components/user/src"
           "components/user/test"
           "projects/command-line/test"],
 :missing []}
```

- `:existing` All existing paths in the workspace that are used in bricks, projects and profiles.
- `:on-disk` All paths to directories within the workspace.
- `missing` All missing paths in the workspace that are used in bricks, projects and profiles but
            does not exist on disk.

## projects

`poly ws get:projects:user-service`

```
{:alias "user-s",
 :base-names {:src ["user-api"], :test ["user-api"]},
 :component-names {:src ["user"], :test ["user"]},
 :config-filename "/Users/joakimtengstrand/source/polylith/examples/doc-example/projects/user-service/deps.edn",
 :deps {"user" {:src {}, :test {}},
        "user-api" {:src {:direct ["user-remote"]},
                    :test {:direct ["user-remote"]}}},
 :is-dev false,
 :is-run-tests true,
 :lib-deps {:src {"org.apache.logging.log4j/log4j-core" {:size 1714164,
                                                         :type "maven",
                                                         :version "2.13.3"},
                  "org.apache.logging.log4j/log4j-slf4j-impl" {:size 23590,
                                                               :type "maven",
                                                               :version "2.13.3"},
                  "org.clojure/clojure" {:size 3914649,
                                         :type "maven",
                                         :version "1.10.3"},
                  "org.clojure/tools.deps.alpha" {:size 60953,
                                                  :type "maven",
                                                  :version "0.12.1003"},
                  "slacker/slacker" {:size 28408,
                                     :type "maven",
                                     :version "0.17.0"}}},
 :lib-imports {:src ["se.example.user-api.api" "slacker.server"],
               :test ["clojure.test" "se.example.user-api.core"]},
 :lines-of-code {:src 0, :test 0, :total {:src 44, :test 26}},
 :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
               "clojars" {:url "https://repo.clojars.org/"}},
 :name "user-service",
 :namespaces {},
 :paths {:src ["bases/user-api/resources"
               "bases/user-api/src"
               "components/user/resources"
               "components/user/src"],
         :test ["bases/user-api/test" "components/user/test"]},
 :project-dir "/Users/joakimtengstrand/source/polylith/examples/doc-example/projects/user-service",
 :type "project"}
```

- `:alias` The alias that is specified in `:projects` in `workspace.edn` for this project.
- `:base-names`
  - `:src` The bases that are included in the project for the `src` context,
           either as paths or included as `:local/root`.
  - `:test` The bases that are included in the project for the `test` context,
            either as paths or included as `:local/root`.
- `:component-names`
  - `:src` The components that are included in the project for the `src` context,
           either as paths or included as `:local/root`. 
  - `:test` The components that are included in the project for the `test` context,
            either as paths or included as `:local/root`.
- `:config-filename` The absolute path to the `deps.edn` config file.
- `:deps` A map that stores brick names with a list with the interfaces each brick uses.
- `:is-dev` Set to `true` for the `development` project.
- `:is-run-tests` Set to `true` if the project is relevant for running tests.
  The `development` project is not included by default (and therefore set to `false`)
  but can be added by passing in `:dev` or by giving it as `projects:dev` or `projects:development`. 
  This flag is also affected by passing in `:all` and `:all-bricks`.
- `:lib-deps` 
  - `:src` Stores a map with the libraries that are used in the project for the `src` context.
    - `:size` The size of this library in bytes.
    - `:type` The type of the library, `maven`, `local` or `git` (`:mvn/version`, `:local/root` and `:git/url`).
    - `:version` The library version:
      - if type is `maven` then version is set to groupId/artifactId.
      - if type is `local` then version is set to `-`.
      - if type is `git` then version is set to the first seven characters in the `sha`.
  - `:test` Stores a map with the libraries that are used in the project for the `test` context.
- `:lib-imports` 
  - `src` All `:lib-imports` taken from the bricks that are included in this project for the `src` context.
  - `test` All `:lib-imports` taken from the bricks that are included in this project for the `test` context.
- `:lines-of-code`
  - `:src` Number of lines of code living in the project's `src` directory.
  - `:test` Number of lines of code living in the project's `test` directory.
  - `:total` Total number of lines of code for all the bricks that are included in this project.
- `:maven-repos` The maven repos that are used by this project. 
  If `:mvn/repos` is specified by a brick that is included in this project,
  then it will automatically turn up in this list.
- `:name` The name of the project directory under the `projects` directory.
- `:namespaces` I the project has a `test` directory (and/or a `src` directory, but they are discouraged to use for projects)
                then the included namespaces are listed here.
- `:paths`
  - `:src` Lists the paths that are either explicitly defined as paths or implicitly defined as `:local/root` bricks, for the `src` context.
  - `:test` Lists the paths that are either explicitly defined as paths or implicitly defined as `:local/root` bricks, for the `test` context.
- `:project-dir` The absolute path to the project directory.
- `:type` Set to "project".

## settings

`poly ws get:settings`

```
{:active-profiles #{"default"},
 :color-mode "dark",
 :compact-views #{},
 :default-profile-name "default",
 :empty-character ".",
 :interface-ns "interface",
 :m2-dir "/Users/joakimtengstrand/.m2",
 :profile-to-settings {"default" {:base-names [],
                                  :component-names ["user"],
                                  :lib-deps {},
                                  :paths ["components/user/src"
                                          "components/user/resources"
                                          "components/user/test"],
                                  :project-names []},
                       "remote" {:base-names [],
                                 :component-names ["user-remote"],
                                 :lib-deps {},
                                 :paths ["components/user-remote/src"
                                         "components/user-remote/resources"
                                         "components/user-remote/test"],
                                 :project-names []}},
 :projects {"command-line" {:alias "cl"},
            "development" {:alias "dev"},
            "user-service" {:alias "user-s"}},
 :tag-patterns {:release "v[0-9]*", :stable "stable-*"},
 :thousand-separator ",",
 :top-namespace "se.example",
 :user-config-filename "/Users/joakimtengstrand/.polylith/config.edn",
 :user-home "/Users/joakimtengstrand",
 :vcs {:auto-add true,
       :branch "issue-66-23",
       :git-root "/Users/joakimtengstrand/source/polylith",
       :name "git",
       :polylith {:branch "master",
                  :repo "https://github.com/polyfy/polylith.git"},
       :stable-since {:sha "f7e8cd7fe83f6d2fdfdedda35fed5806ac418964",
                      :tag "stable-jote"}}}
```

- `:active-profiles` If any profiles are defined in `./deps.edn` then the active profiles(s) are listed here.
- `:color-mode` The color mode specified in `~/.polylith/config.edn`.
- `:compact-views` The set of views that should be shown in a more compact way, specified in `workspace.edn`.
- `:default-profile-name` The name of the default profile name, specified in `workspace.edn`.
- `:empty-character` The character used to represent empty space in ouput from e.g. the `libs` command, specified in `workspace.edn`.
- `:interface-ns` The name of the namespace/package that are used to represent interfaces, specified in `workspace.edn`.
- `:m2-dir` Maven user root directory. Set to "~/.m2" by default, but can be overridden in `~/.polylith/config.edn`.
- `:profile-to-settings` A map with profile name as key and profile definition as value, specified as aliases starting with a `+` in `./deps.edn`:
  - `:base-names` The bases that are referenced from the specified paths.
  - `:component-names` The components that are referenced from the specified paths.
  - `:lib-deps` The library dependencies specified by the key `:extra-deps`.
  - `:paths` The paths specified by the key `:extra-paths`.
  - `:project-names` The projects that are referenced from the specified paths.
- `:projects` A map with extra information per project, specified in `workspace.edn`.
  - `:alias` The alias for a project, used by e.g. the `info` command.
  - `:test`
    - `:include` Specifies which bricks should be included when running the `test` command.
                 Empty if no bricks, missing if all bricks.
- `:tag-patterns` The tag patterns that are specified in `workspace.edn`.
- `:thousand-separator` Used by numbers >= 1000 (e.g. the KB column in the `libs` command) specified in `workspace.edn`.
- `:top-namespace` The top namespace for the workspace, specified in `workspace.edn`.
- `:user-config-filename` The full path to the user config name.
- `:user-home` The user home, specified by the `user.home` environment variable.
- `:vcs`
  - `:auto-add` Set to `true` if files and directories created by the `create` command
                should be automatically added to `git`. Specified in `workspace.edn`.
  - `:branch` The name of the git branch.
  - `:git-root` The root of the git repository.
  - `:name` Set to "git".
  - `:polylith`
    - `:branch` Set `master` or `BRANCH` if `branch:BRANCH` is given.
                The branch is used when calculating the latest `sha` in key 
                `:aliases > :poly > :extra-deps > sha`.
    - `:latest-sha` If `:latest-sha` is given, then the latest `sha` from the 
                    Polylith repo (`https://github.com/polyfy/polylith.git`) is retrieved.
    - `:repo` Set to "https://github.com/polyfy/polylith.git".
  - `:stable-since`
    - `:sha` The latest stable point in time.
    - `:tag` The `tag` for the latest stable point in time (if given).
  
## user-input

`poly ws get:user-input`

```
{:args ["ws" "get:user-input"],
 :cmd "ws",
 :get "user-input",
 :is-all false,
 :is-dev false,
 :is-latest-sha false,
 :is-no-exit false,
 :is-run-all-brick-tests false,
 :is-run-project-tests false,
 :is-search-for-ws-dir false,
 :is-show-brick false,
 :is-show-loc false,
 :is-show-project false,
 :is-show-resources false,
 :is-show-workspace false,
 :is-verbose false,
 :selected-profiles #{},
 :selected-projects #{},
 :unnamed-args []}
```

- `:args` The arguments to the `poly` tool where the first argument is the command.
- `:cmd` The first argument to the `poly` command.
- `:get` The `ARGS` of `get:ARGS` if given.
- `:is-all` Set to `true` if `:all` is given.
- `:is-dev` Set to `true` if `:dev` is given.
- `:is-latest-sha` Set to `true` if `:latest-sha` is given.
- `:is-no-exit` Set to `true` if `:no-exit` is given. This will prevent the `poly` command
                from exiting with `System/exit`.
- `:is-run-all-brick-tests` Set to `true` if `:all` or `:all-bricks` are given.
- `:is-run-project-tests` Set to `true` if `:all` or `:project` are given.
- `:is-search-for-ws-dir` Set to `true` if `::` is given.
- `:is-show-brick` Set to `true` if `:brick` is given. Used by `poly help deps :brick`
                   to show help for the `deps` command when `brick:BRICK` is given.
- `:is-show-loc` Set to `true` if `:loc` is given. If given, then the `info` command will show the number of lines of code.
- `:is-show-project` Set to `true` if `:project` is given. Used by `poly help deps :project`
                     to show help for the `deps` command when `project:PROJECT` is given.
- `:is-show-resources` Set to `true` if `:r` or `:resources` is given. This will tell the `info` command
                       to show the `r` status flag.
- `:is-show-workspace` Set to `true` if `:workspace` is given. Used by `poly help deps :workspace`
                       to show help for the `deps` command when `workspace:WORKSPACE` is given.
- `:is-verbose` Experimental at the moment. Can be used on combination with the `test` command
                to show extra information.
- `:selected-profiles` A list with the passed in profiles, e.g. `["defult" "extra"]` if `poly info +default +extra` is executed.
- `:selected-projects` The list of projects given by `project:PROJECT` and/or `:dev` (handled as "dev" if given).
- `:unnamed-args` All given arguments that don't contain a `:`.

## version

`poly ws get:version`

```
{:release {:date "2021-07-27",
           :major 0,
           :minor 2,
           :patch 0,
           :revision "alpha10.issue66.22"},
           :name "0.2.0.alpha10.issue66.22",
 :ws {:breaking 1, 
      :non-breaking 0, 
      :type :toolsdeps2}}
```

- `:release`
  - `:date` The date of the release in the format `yyyy-mm-dd`.
  - `:major` The major version, set to zero.
  - `:minor` Increased by one if any breaking changes.
  - `:patch` Increased by one for each release within a given `major.minor`.
  - `:revision` What comes after `major.minor.path`.
  - `:name` The full name of the release.
- `:ws` Versioning of the internal `ws` format, returned by `poly ws`.
  - `:breaking` Increased by one if introducing a non backwards compatible change of the `ws` format.
                The goal is that `1` is the last breaking change.
  - `:non-breaking` Increased by one when a non breaking change is added to the `ws` format.
  - `:type` Set to `:toolsdeps1` if a workspace created by `v0.1.0-alpha9` or earlier.
            Set to `:toolsdeps2` if a workspace created by `v0.2.0-alpha10` or later.

If the workspace has the old `:toolsdeps1` format (created by `v0.1.0-alpha9` or earlier) 
then it will be transformed to the latest format and the `:from` key is added:

```
{:from {:ws {:type :toolsdeps1}
 ...}
```

- `:from`
  - `:ws`
    - `:type` Set to ":toolsdeps1"

If the workspace is read from a `ws file` that was created by `v0.1.0-alpha9` or earlier,
then the `ws` structure is transformed to the new format and the `:from` key is added:

`poly ws get:version ws-file:ws1.edn`

```
{:from {:release-name "0.1.0-alpha9",
        :ws {:breaking 0, 
             :non-breaking 0, 
             :type :toolsdeps1}}
 ...
```

- `:from`
  - `:release-name` The version of the `poly` tool from which this `ws` file was created.
  - `:ws`
    - `:breaking` The breaking version of the original `ws` format.
    - `:non-breaking` The non-breaking version of the original `ws` format.
    - `:type` The type of the original `ws` file.

## ws-dir

`poly ws get:ws-dir`

```
"/Users/joakimtengstrand/source/polylith/examples/doc-example"
```

The absolute path of the workspace directory.

## ws-local-dir

`poly ws get:ws-local-dir`

```
"examples/doc-example"
```

If the workspace lives at the same level as the git root, which is the case if we create
a workspace with `poly create workspace ...`, then this attribute is not set.
If the workspace lives inside the git root as a directory or sub directory, then it
is set to the relative path to the git root.

## ws-reader

`poly ws get:ws-reader`

```
{:file-extensions ["clj" "cljc"],
 :language "Clojure",
 :name "polylith-clj",
 :project-url "https://github.com/polyfy/polylith",
 :type-position "postfix"}
```

This structure explains different aspects of the tool that created this `ws` structure
(the `poly` tool in this case) and the idea is that new tooling could support the
`ws` format and populate this structure so that it can be used by external tooling.

- `:file-extensions` Lists the supported file extensions.
- `:language` The name of the supported language.
- `:name` The name of the workspace reader.
- `:project-url` The url to the workspace reader tool.
- `:type-position` Set to `postfix` because types (type hints) come before the arguments, 
                   in Clojure, e.g. `^String arg`. In some other languages like Scala, 
                   the types come after the arguments.
