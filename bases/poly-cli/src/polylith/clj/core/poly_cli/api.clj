(ns polylith.clj.core.poly-cli.api
  "An exec fn API for the poly tool that can be used
  with -X and -T options."
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

(defn check   [exec-args] (->command "check"   exec-args))
(defn create  [exec-args] (->command "create"  exec-args))
(defn deps    [exec-args] (->command "deps"    exec-args))
(defn diff    [exec-args] (->command "diff"    exec-args))
(defn help    [exec-args] (->command "help"    exec-args))
(defn info    [exec-args] (->command "info"    exec-args))
(defn libs    [exec-args] (->command "libs"    exec-args))
(defn migrate [exec-args] (->command "migrate" exec-args))
;; shell -> prompt
(defn shell   [exec-args] (->command "prompt"  exec-args))
(defn test    [exec-args] (->command "test"    exec-args))
(defn version [exec-args] (->command "version" exec-args))
(defn ws      [exec-args] (->command "ws"      exec-args))
