(ns ^:no-doc polylith.clj.core.poly-cli.api
  "A poly tool API for the Clojure CLI's -X and -T options."
  (:refer-clojure :exclude [test])
  (:require [clojure.string :as str]
            [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.util.interface.exception :as ex]))

(set! *warn-on-reflection* true)

(defn- ->named-arg
  "Given a key and a value, return a poly tool style
  named argument string."
  [k v]
  (str (name k)
       ":"
       (if (coll? v) (str/join ":" (map str v)) v)))

(defn- ->unnamed-args
  "Given a key's value, return a vector of poly tool
  style unnamed arguments."
  [v]
  (if (vector? v)
    v
    (vec (str/split (str v) #":"))))

(defn argument-mapping
  "Map exec args to a vector of strings that represent the
  poly tool's command-line arguments. We accept symbols for
  the keys (or keywords, we don't care). We document it all
  as symbols.

  In general, a boolean-valued key becomes a :flag argument,
  string-valued keys become key:value arguments, and vector-
  valued keys become key:val:val:val arguments.

  Special cases:
   entity   -- used to provide a :-separated list of leading
               arguments; could also be a vector of strings
   entities -- alternative to entity for vector values
   profile  -- identify +-prefixed profile names
   profiles -- alternative to :profile for vector values
   ws       -- alternative to ::

  For create -- to make life easier:
   c s -- component name:s
   b s -- base name:s
   p s -- project name:s
   w s -- workspace name:s"
  [exec-args]
  (reduce-kv (fn [args k v]
               (case (name k)
                 ;; entities go at the front:
                 ("entity" "entities")
                 (into (->unnamed-args v) args)
                 ;; profiles can go at the end, with + added:
                 ("profile" "profiles")
                 (into args (map #(str "+" %) (->unnamed-args v)))
                 ;; project can be a flag or a named arg:
                 ("project" "projects")
                 (if (boolean? v)
                   (conj args ":project") ; flag
                   (conj args (->named-arg "project" v)))
                 ;; shorthands for create:
                 ("b" "c" "p" "w")
                 (into [(name k) (str "name:" v)] args)
                 ;; exec-arg friendly version of :: flag argument:
                 "ws"
                 (conj args "::")
                 ;; otherwise it's a regular flag or named arg:
                 (if (boolean? v)
                   (conj args (str ":" (name k)))
                   (conj args (->named-arg k v)))))
             []
             exec-args))

(defn- ->command
  "Given a command name and the exec args, map those to
  internal arguments, and execute the command."
  [cmd exec-args]
  (let [args  (cons cmd (argument-mapping exec-args))
        input (user-input/extract-params args)]
    ;; identical behavior to core/-main:
    (try
      (if (:is-no-exit input)
        (-> input command/execute-command)
        (-> input command/execute-command System/exit))
      (catch Exception e
        (ex/print-exception (:cmd input) e)
        (when (-> input :is-no-exit not)
          (System/exit 1))))))

;; these are the exec fns that correspond to commands:

(defn check
  "Validates the workspace.

  clojure -Tpoly check

Prints 'OK' and returns 0 if no errors were found.
If errors or warnings were found, show messages and return the error code,
or 0 if only warnings. If internal errors, 1 is returned.

For more detail: clojure -Tpoly help entity check"
  [exec-args]
  (->command "check"   exec-args))

(defn create
  "Creates a component, base, project or workspace.

  clojure -Tpoly create entity TYPE name NAME [ARGS]
    TYPE = c[omponent] -> Creates a component.
           b[ase]      -> Creates a base.
           p[roject]   -> Creates a project.
           w[orkspace] -> Creates a workspace.

    ARGS = Varies depending on TYPE.

Shorthand:
  clojure -Tpoly create c NAME -> Creates a component.
                        b NAME -> Creates a base.
                        p NAME -> Creates a project.
                        w NAME -> Creates a workspace.

For more detail:
  clojure -Tpoly help entity create
  clojure -Tpoly help entity create:base
  clojure -Tpoly help entity create:component
  clojure -Tpoly help entity create:project
  clojure -Tpoly help entity create:workspace"
  [exec-args]
  (->command "create"  exec-args))

(defn deps
  "Shows dependencies.

  clojure -Tpoly deps [project PROJECT] [brick BRICK]
    (omitted) = Show workspace dependencies.
    PROJECT   = Show dependencies for specified project.
    BRICK     = Show dependencies for specified brick.

For more detail:
  clojure -Tpoly help entity deps
  clojure -Tpoly help entity deps brick true
  clojure -Tpoly help entity deps project true
  clojure -Tpoly help entity deps project true brick true"
  [exec-args]
  (->command "deps"    exec-args))

(defn diff
  "Shows changed files since the most recent stable point in time.

  clojure -Tpoly diff

Internally, it executes 'git diff SHA --name-only' where SHA is the SHA-1
of the first commit in the repository, or the SHA-1 of the most recent tag
that matches the default pattern 'stable-*'.

For more detail: clojure -Tpoly help entity diff"
  [exec-args]
  (->command "diff"    exec-args))

(defn help
  "Display help.

For more detail: clojure -Tpoly help"
  [exec-args]
  (->command "help"    exec-args))

(defn info
  "Shows workspace information.

  clojure -Tpoly info [ARGS]
    ARGS = loc true   -> Shows the number of lines of code for each brick
                         and project.

In addition to :loc, all the arguments used by the 'test' command
can also be used as a way to see what tests will be executed.

For more detail: clojure -Tpoly help entity info"
  [exec-args]
  (->command "info"    exec-args))

(defn libs
  "Shows all libraries that are used in the workspace.

  clojure -Tpoly libs [all true]
    all true = View all bricks, including those without library dependencies.

For more detail: clojure -Tpoly help entity libs"
  [exec-args]
  (->command "libs"    exec-args))

(defn migrate
  "Migrates a workspace to the latest version.

  clojure -Tpoly migrate

If the workspace hasn't been migrated already, then this command will create a new
./workspace.edn file + a deps.edn file per brick.

For more detail: clojure -Tpoly help entity migrate"
  [exec-args]
  (->command "migrate" exec-args))

(defn shell
  "clojure -Tpoly shell

Starts an interactive shell with a prompt that is the name of the current
workspace, e.g.:
  myworkspace$

For more detail: clojure -Tpoly help entity shell"
  [exec-args]
  (->command "shell"  exec-args))

(defn test
  "Executes brick and/or project tests.

  clojure -Tpoly test [ARGS]

The brick tests are executed from all projects they belong to except for the development
project (if :dev true is not passed in).

For more detail: clojure -Tpoly help entity test"
  [exec-args]
  (->command "test"    exec-args))

(defn version
  "Display the version.

For more detail: clojure -Tpoly help entity version"
  [exec-args]
  (->command "version" exec-args))

(defn ws
  "Prints or writes the workspace as data.

  clojure -Tpoly ws [get ARG] [out FILE] [latest-sha true] [branch BRANCH]
    ARG = keys  -> Lists the keys for the data structure:
                   - If it's a hash map, it returns all its keys.
                   - If it's a list and its elements are hash maps,
                     it returns a list with all the :name keys.

For more detail: clojure -Tpoly help entity ws"
  [exec-args]
  (->command "ws"      exec-args))
