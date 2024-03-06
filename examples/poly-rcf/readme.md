
Example on how to run [Hyperfiddle rcf](https://github.com/hyperfiddle/rcf) tests with [Polylith poly tool](https://polylith.gitbook.io/poly/).

I figured out that I could run the tests from the development project with `clojure -M:poly test :dev project:dev`. I then created `deps-rcf.edn` and `deps-261.edn` and tested both with the same statement by first copying them to `deps.edn`. I figured out that I had to add hyperfiddle/rcf + slf4j to `bases/empty/deps.edn` for the `poly261` to work. I also removed "src" from `:extra-paths` in the `:test` alias in `bases/rcf/deps.edn`, because it wasn't needed.

With these changes, the workspace (using the original `deps.edn` file) behaves like this:
* I can run the `rcf` project with `clojure -M:poly test project:rcf`.
* I can run the `poly261` project with `clojure -M:poly test project:poly261`.
* If I run both `rcf` and `poly261` with `clojure -M:poly test :all`, then `rfc` works, but `poly261` fails.

Before the change in `bases/empty/deps.edn`, I could reproduce the problem in [issue 261](https://github.com/polyfy/polylith/issues/261) by executing `clojure -M:poly test :all :verbose` from the `examples/poly-rcf` directory.

This workspace has been cloned and slightly modified from [Poly 261 - Classpath stability for tests](https://github.com/ieugen/poly-rcf/pull/1):
* Renamed the `issue-261` base to `empty`.
* Don't include `sqldb` as a dependency from `empty` in its `deps.edn`.
* Changed top namespace from `poly-rcf.rcf` to `poly-rcf`.
* Made sure all bricks live in the correct namespace (the `rcf` base needed one extra `rcf` directory).
* Use the latest version of the poly tool, in the `:poly` alias in ./deps.edn.
* Removed unused `resources` and references to project `test` directories.
* Added hyperfiddle/rcf + slf4j to `bases/empty/deps.edn`.
* Added the `monolith` directory, so that we can execute the code as a monolith with `clj -X:test`.
