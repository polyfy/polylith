
## Included since 0.2.22

The biggest change in 0.3.0 is support for ClojureScript :
- The poly tool now handles ClojureScript (.cljs) source files as well as _package.json_ configuration files.
- Changes to the `libs` command:
  - npm dependencies are now included in the list of library dependencies
  - the `:outdated` parameter also checks for outdated npm dependencies
  - the `:update` parameter also updates npm dependencies in _package.json_ files
    - `:keep-lib-versions` also works for npm dependencies 
    - the `libraries:LIBS` filtering works as expected (outdated npm libraries are suggested when run from a shell) 
- Configuration from _package.json_ files are stored in the workspace structure, and can be retrieved by e.g. `ws get:configs:components:web-ui:package` or `ws get:configs:projects:development:package` (example taken from the [realworld example app](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app/tree/cljs-frontend)).

Be sure to update to the latest versions of the [External](https://github.com/seancorfield/polylith-external-test-runner) and [Kaocha](https://github.com/imrekoszo/polylith-kaocha) test runners if you use any of these.

### Issues and PRs:
- Add support for ClojureScript, issue [482](https://github.com/polyfy/polylith/issues/481)
- Fix typo in the documentation, PR [560](https://github.com/polyfy/polylith/pull/560)

### Other changes
- 

### Doc updates
- Changed the sha for Sean's External test runner to `c97747aa2b1fdf03c46c7e435cca7c2608740a2a` in [this](https://cljdoc.org/d/polylith/clj-poly/0.3.0/doc/test-runners#_use_a_custom_test_runner) section.
  This version ignores cljs namespaces, which would otherwise have caused errors when running the `test` command.
- Mention that tests should be run with Deps [here](https://cljdoc.org/d/polylith/clj-poly/0.3.0/doc/testing#cursive-users).
