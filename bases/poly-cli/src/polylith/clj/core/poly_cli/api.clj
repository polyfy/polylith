(ns polylith.clj.core.poly-cli.api
  "A poly tool API for the Clojure CLI's -X and -T options."
  (:refer-clojure :exclude [test])
  (:require [clojure.string :as str]
            [polylith.clj.core.command.interface :as command]))

(set! *warn-on-reflection* true)

(defn- str-coll
  "Given an argument value, coerce it to a vector and
  make all the elements strings."
  [arg]
  (mapv str (if (vector? arg) arg [arg])))

;; the mappings here should produce keywords that match
;; polylith.clj.core.user-input.core/extract-params so
;; that polylith.clj.core.command.interface/execute-command
;; can be called directly from this exec fn API:

(defn- argument-mapping
  "Map exec args to the sort of internal arguments that
  the poly tool expects. This is made harder by the way
  that poly has a somewhat 'unique' approach to command
  line argument handling!

  Keyword differences:
  :brick    bool -- :is-show-brick true/false
  :brick    s    -- :brick name
  :entity   s    -- needed for the create and help commands
  :profile  s    -- :selected-profiles (singular for convenience)
  :profiles [s]  -- :selected-profiles (b/c no unnamed args)
                    (may be just a single symbol/string)
  :project  bool -- :is-show-project true/false
  :project  s    -- :selected-projects (singular for convenience)
  :projects [s]  -- :selected-projects (b/c avoid :-separated)
                    (may be just a single symbol/string)
  :ws       bool -- :is-search-for-ws-dir (b/c :: is not legal)

  For create -- to make life easier:
  :c s -- component name:s
  :b s -- base name:s
  :p s -- project name:s
  :w s -- workspace name:s

  Items not yet properly handled:
  :args -- there's no concept of sequential ordered args
    (a vector of the command + entity is passed)
  :replace -- relies on non-interface function
  :unnamed-args -- there's no concept of sequential ordered args
    (a vector of the command + entity is passed)"
  [exec-args]
  (reduce-kv (fn [m k v]
               (cond-> m
                 ;; add is-* variant for all booleans:
                 (boolean? v)
                 (assoc (keyword (str "is-" (name k))) v)
                 ;; add is-show-* variant:
                 (and (boolean? v)
                      (contains? #{:brick :workspace :project :loc :resources} k))
                 (assoc (keyword (str "is-show-" (name k))) v)
                 ;; some special case boolean flags:
                 (and (boolean? v)
                      (contains? #{:ws} k))
                 (assoc :is-search-for-ws-dir v)
                 (and (boolean? v)
                      (contains? #{:all :all-bricks} k))
                 (assoc :is-run-all-brick-tests v)
                 (and (boolean? v)
                      (contains? #{:all :project} k))
                 (assoc :is-run-project-tests v)
                 (and (boolean? v)
                      (contains? #{:r} k))
                 (assoc :is-show-resources v)
                 ;; vectorize and stringify skip:
                 (contains? #{:skip} k)
                 (assoc :skip (when v (str-coll v)))
                 ;; profile(s):
                 (contains? #{:profile} k)
                 (update :selected-profiles conj (str v))
                 (contains? #{:profiles} k)
                 (update :selected-profiles into (str-coll v))
                 ;; project(s) + dev (flag):
                 (and (not (boolean? v))
                      (contains? #{:project} k))
                 (update :selected-projects conj (str v))
                 (contains? #{:projects} k)
                 (update :selected-projects into (str-coll v))
                 (contains? #{:dev} k)
                 (cond-> v (update :selected-projects conj "dev"))
                 ;; stringify any possible symbol args:
                 ;; (this is just a usability issue for exec args)
                 (and (not (boolean? v))
                      (contains? #{:get :brick :branch :color-mode
                                   :entity
                                   :fake-sha :interface :name :out
                                   :since :top-ns :ws-dir :ws-file} k))
                 (update k str)
                 ;; special handling for create shorthands:
                 (contains? #{:c :b :p :w} k)
                 (assoc :entity (name k) :name (str v))))
             ;; defaults
             (merge {:selected-profiles #{}
                     :selected-projects #{}
                     :unnamed-args []}
                    exec-args)
             exec-args))

(defn- ->command
  "Given a command name and the exec args, map those to
  internal arguments, and execute the command."
  [cmd exec-args]
  (let [args     (argument-mapping exec-args)
        ;; help is a special case for :entity as it can
        ;; have multiple arguments, so we :-separate them
        entities (when-let [entity (:entity args)]
                   (str/split entity #":"))
        args     (assoc args
                        :args (cond-> [cmd]
                                entities (into entities))
                        :cmd  cmd)
        exit
        (command/execute-command args)]
    ;; this mirrors the cli/-main behavior:
    (when-not (:is-no-exit args)
      (System/exit exit))))

;; these are the exec fns that correspond to commands:

(defn check
  "Validates the workspace.

  clojure -Tpoly check

Prints 'OK' and returns 0 if no errors were found.
If errors or warnings were found, show messages and return the error code,
or 0 if only warnings. If internal errors, 1 is returned.

For more detail: clojure -Tpoly help :entity check"
  [exec-args]
  (->command "check"   exec-args))

(defn create
  "Creates a component, base, project or workspace.

  clojure -Tpoly create :entity TYPE :name NAME [ARGS]
    TYPE = c[omponent] -> Creates a component.
           b[ase]      -> Creates a base.
           p[roject]   -> Creates a project.
           w[orkspace] -> Creates a workspace.

    ARGS = Varies depending on TYPE.

Shorthand:
  clojure -Tpoly create :c NAME -> Creates a component.
                        :b NAME -> Creates a base.
                        :p NAME -> Creates a project.
                        :w NAME -> Creates a workspace.

For more detail:
  clojure -Tpoly help :entity create
  clojure -Tpoly help :entity create:base
  clojure -Tpoly help :entity create:component
  clojure -Tpoly help :entity create:project
  clojure -Tpoly help :entity create:workspace"
  [exec-args]
  (->command "create"  exec-args))

(defn deps
  "Shows dependencies.

  clojure -Tpoly deps [:project PROJECT] [:brick BRICK]
    (omitted) = Show workspace dependencies.
    PROJECT   = Show dependencies for specified project.
    BRICK     = Show dependencies for specified brick.

For more detail:
  clojure -Tpoly help :entity deps
  clojure -Tpoly help :entity deps :brick true
  clojure -Tpoly help :entity deps :project true
  clojure -Tpoly help :entity deps :project true :brick true"
  [exec-args]
  (->command "deps"    exec-args))

(defn diff
  "Shows changed files since the most recent stable point in time.

  clojure -Tpoly diff

Internally, it executes 'git diff SHA --name-only' where SHA is the SHA-1
of the first commit in the repository, or the SHA-1 of the most recent tag
that matches the default pattern 'stable-*'.

For more detail: clojure -Tpoly help :entity diff"
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
    ARGS = :loc true   -> Shows the number of lines of code for each brick
                          and project.

In addition to :loc, all the arguments used by the 'test' command
can also be used as a way to see what tests will be executed.

For more detail: clojure -Tpoly help :entity info"
  [exec-args]
  (->command "info"    exec-args))

(defn libs
  "Shows all libraries that are used in the workspace.

  clojure -Tpoly libs [:all true]
    :all true = View all bricks, including those without library dependencies.

For more detail: clojure -Tpoly help :entity libs"
  [exec-args]
  (->command "libs"    exec-args))

(defn migrate
  "Migrates a workspace to the latest version.

  clojure -Tpoly migrate

If the workspace hasn't been migrated already, then this command will create a new
./workspace.edn file + a deps.edn file per brick.

For more detail: clojure -Tpoly help :entity migrate"
  [exec-args]
  (->command "migrate" exec-args))

;; shell -> prompt
(defn shell
  "clojure -Tpoly shell

Starts an interactive shell with a prompt that is the name of the current
workspace, e.g.:
  myworkspace$>

For more detail: clojure -Tpoly help :entity prompt"
  ;; Once the interactive poly tool changes from prompt to shell, this
  ;; docstring needs to be updated, as well as the "prompt" cmd below!
  [exec-args]
  (->command "prompt"  exec-args))

(defn test
  "Executes brick and/or project tests.

  clojure -Tpoly test [ARGS]

The brick tests are executed from all projects they belong to except for the development
project (if :dev true is not passed in).

For more detail: clojure -Tpoly help :entity test"
  [exec-args]
  (->command "test"    exec-args))

(defn version
  "Display the version.

For more detail: clojure -Tpoly help :entity version"
  [exec-args]
  (->command "version" exec-args))

(defn ws
  "Prints or writes the workspace as data.

  clojure -Tpoly ws [:get ARG] [:out FILE] [:latest-sha true] [:branch BRANCH]
    ARG = keys  -> Lists the keys for the data structure:
                   - If it's a hash map, it returns all its keys.
                   - If it's a list and its elements are hash maps,
                     it returns a list with all the :name keys.

For more detail: clojure -Tpoly help :entity ws"
  [exec-args]
  (->command "ws"      exec-args))
