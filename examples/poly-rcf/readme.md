
Example on how to run [Hyperfiddle rcf](https://github.com/hyperfiddle/rcf) tests with [Polylith poly tool](https://polylith.gitbook.io/poly/).

To reproduce [issue 261](https://github.com/polyfy/polylith/issues/261):
* Execute `clojure -M:poly test :all :verbose` from the `examples/poly-rcf` directory.

To execute the tests so that they work:

* From the `development` project: `clojure -M:poly test :dev project:development`
* If we organise the code as a monolith, from `examples/poly-rcf/monolith` with `clj -X:test`.

This workspace has been cloned and slightly modified from [Poly 261 - Classpath stability for tests](https://github.com/ieugen/poly-rcf/pull/1):
* Renamed the `issue-261` base to `empty`.
* Don't include `sqldb` as a dependency from `empty` in its `deps.edn`.
* Changed top namespace from `poly-rcf.rcf` to `poly-rcf`.
* Made sure all bricks live in the correct namespace (the `rcf` base needed one extra `rcf` directory).
* Use the latest version of the poly tool, in the `:poly` alias in ./deps.edn.
* Removed unused `resources` and references to project `test` directories.
* Added `:all` to the `test` command, to make sure all tests are executed.
* Added the `monolith` directory.
