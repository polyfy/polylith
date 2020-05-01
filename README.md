# Polylith

This is an experimental repository for trying [Polylith](https://github.com/tengstrand/lein-polylith) with Clojure's tools.deps.

You can checkout the `clojure-deps` branch for the [Realworld Example](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app/tree/clojure-deps) project which uses this experimental library to compile and test:
```sh
git clone https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app.git
cd clojure-polylith-realworld-example-app
git checkout clojure-deps
```

## Usage

Navigate to a Polylith project directory that is configured with `deps.edn` (e.g. `clojure-polylith-realworld-example-app`). Add this repository to the project as a git dependency under the `dev` alias (or any alias you prefer) in the project `deps.edn` file, e.g.:
```clojure
{...
 :aliases {:dev {:extra-deps {furkan3ayraktar/polylith {:git/url "https://github.com/furkan3ayraktar/polylith.git"
                                                        :sha "89a91b1c519b338eb5a15c90cb97559c09484e89"}}}}
 ...
}
```

### compile

This command allow you to compile either the entire Polylith project (if you don't provide any arguments) or a specific service or environment defined in the deps.edn. Execute this from your Polylith project root, e.g. `clojure-polylith-realworld-example-app`:
```sh
clj -A:dev -m polylith.main compile realworld-backend
```

### test

This command lets you run tests under a specific service or environment defined in `deps.edn`, e.g.:
```sh
clj -A:dev -m polylith.main test realworld-backend
```
