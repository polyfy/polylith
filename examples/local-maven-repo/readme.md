
<h1>local-maven-repo</h1>

Workspace used to verify [issue 532](https://github.com/polyfy/polylith/issues/532)

In the deps.edn file, we have the key `:mvn/local-repo` with the value "/Users/joakimtengstrand/maven-repo". To verify that it will use the local maven repo, do this:

* Change "/Users/joakimtengstrand/maven-repo" to an existing directory (e.g. one that you create).
* Ensure that you have internet access.
* Run `clojure -M:poly test :dev` from the root. This will download all libraries to the local repo (directory) and run one test successfully.
* Turn off internet access.
* Run the tests again, and they should run successfully
* Remove e.g. directory `org/clojure/clojure/1.10.0` from the local repo, and run the test again. This time the tests fails, because we have a missing library in the local repo.

The tests can also be run with `clojure -M:test` using `cognitect-labs/test-runner`, which will use the clojure version specified in the `:deps` key in deps.edn.
