
## Included since 0.2.22

The biggest change in 0.3.0 is support for ClojureScript.
The poly tool now handles ClojureScript (.cljs) source files as well as _package.json_ configuration files.

> Ensure to use the latest versions of the [External](https://github.com/seancorfield/polylith-external-test-runner) and [Kaocha](https://github.com/imrekoszo/polylith-kaocha) test runners (if you use any of these).

Main changes:

- The `create workspace` command:
  - If `dialects:DIALECTS` is provided, it will be used to set `:dialects` in workspace.edn to `DIALECTS`, e.g. `["clj", "cljs"]` if `create workspace name:NAME top-ns:TOP-NS dialects:clj:cljs`.
    - Only the dialects selected (Clojure/ClojureScript) will be read from disk.
    - If no dialect is specified, `["clj"]` will be used.
    - If any of the values are set to `"cljs"` (`["cljs"]` or `["clj" cljs"]`) a `package.json` file will be created. 
   - The new attribute `:template-data` is set to the initial value `{:clojure-ver "1.12.2", :shadow-cljs-ver "^3.2.0"}`.
     You can add your own set of key/value pairs or update the existing, which are then used by Selmer in the `create` command.
- The `create component`, `create base`, and `create project` commands:
  - If `DIALECT` in `dialect:DIALECT` is set to `cljs`, a `package.json` file will be created.
  - Suggest the `dialect` argument in the shell, if `cljs` is one of the `:dialects` in workspace.edn.
  - If `dialect` is not specified, `clj` will be used.
- The `libs` command:
  - npm dependencies are now included in the list of library dependencies (read from `package.json` files).
  - the `:outdated` parameter also checks for outdated npm dependencies.
  - the `:update` parameter also updates npm dependencies in _package.json_ files.
    - `:keep-lib-versions` also works for npm dependencies.
    - the `libraries:LIBS` filtering works as expected (outdated npm libraries are suggested when run from a shell).
- Configuration from _package.json_ files are stored in the workspace structure and can be retrieved by `ws get:configs:ENTITY:NAME:package` where `ENTITY` is `workspace`, `projects`, `components`, or `bases`, and `TYPE` is the name of the workspace/project/component/base.

> For existing workspaces:
> - Set `:dialects` in `workspace.edn` to `["clj"]`, `["cljs"]`, or `["clj" "cljs"]` to instruct the `poly` tool which type of source files to read (Clojure and/or ClojureScript). 
> - Set `:template-data` in `workspace.edn` to e.g. `{:clojure-ver "1.12.2", :shadow-cljs-ver "^3.2.0"}`.
  More key/value pairs can be added, to be used by the Selmer templates in the `create` command.
> - To support ClojureScript, create a `package.json` file to your workspace root as well as to projects, components, and bases, see the [RealWorld](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app/tree/cljs-frontend) app as an example.

### Issues and PRs:
- Add support for ClojureScript, issue [482](https://github.com/polyfy/polylith/issues/481)
- Fix typo in the documentation, PR [560](https://github.com/polyfy/polylith/pull/560)

### Other changes
- Ensure that running `poly test :verbose` displays the paths correctly after "# paths:" by making the collection serializable.

### Doc updates
- Changed the sha for Sean's External test runner to `c97747aa2b1fdf03c46c7e435cca7c2608740a2a` in [this](https://cljdoc.org/d/polylith/clj-poly/0.3.0/doc/test-runners#_use_a_custom_test_runner) section.
  This version ignores cljs namespaces, which would otherwise have caused errors when running the `test` command.
- Mention that tests should be run with Deps [here](https://cljdoc.org/d/polylith/clj-poly/0.3.0/doc/testing#cursive-users).
- Updated links to use the [cljs-frontend](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app/tree/cljs-frontend) branch of the Realworld example app.
