# Polylith

This is an experimental repository for trying [Polylith](https://github.com/tengstrand/lein-polylith) with Clojure's tools.deps.

You can check `clojure-deps` branch under [Realworld Example](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app/tree/clojure-deps) project which uses this experimental library to compile and test.

## Usage

Navigate to a Polylith project directory, configured with using deps.edn. Add this repository to the project as a git dependency under dev alias (or any alias you prefer).

### compile

This command allow you to compile either the entire Polylith project (if you don't provide any arguments) or a specific service or environment defined in the deps.edn.

```sh
clj -A:dev -m polylith.main compile backend-service
```

### test

This command lets you run tests under a specific service or environment defined in the deps.edn

```sh
clj -A:dev -m polylith.main test backend-service
```
