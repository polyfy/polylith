(ns polylith.clj.core.releases.interface)

;; Workspace attributes:
;;   ENTITIES = bases, components, or projects.
;;   ENTITY = a base, component, or project name.
;;   NAMESPACE = namespace name.
;;   SOURCE = src or test.

(def releases
  {:0.2.18-SNAPSHOT
   [{:name "ws-2.0"
     :attributes [{:attribute "ws-type"
                   :description "Added"}
                  {:attribute "projects:PROJECT:deps:BRICK:SOURCE:missing-ifc-and-bases"
                   :description "Renamed from missing-ifc."}
                  {:attribute "ENTITIES:ENTITY:namespaces:SOURCE:NAMESPACE:file-path"
                   :description "The ws-dir part was removed from the file path."}
                  {:attribute "ENTITIES:ENTITY:namespaces:SOURCE:NAMESPACE:is-invalid"
                   :description "Added"}
                  {:attribute "ENTITIES:ENTITY:namespaces:SOURCE:NAMEPSPACE:is-ignored"
                   :description "Added"}
                  {:attribute "version:tool"
                   :description "Added"}
                  {:attribute "version:release:snapshot"
                   :description "Added"}
                  {:attribute "version:test-runner-api"
                   :description "Added"}
                  {:attribute "bases:BASE:base-deps"
                   :description "Added"}
                  {:attribute "configs"
                   :description "Added"}
                  {:attribute "releases"
                   :description "Added"}]}
    {:name "issue-187"
     :url "https://github.com/polyfy/polylith/issues/187"
     :description "Support for more than one interface (always accept 'interface' and 'ifc' + what's specified in :interface-ns)."}
    {:name "issue-205"
     :url "https://github.com/polyfy/polylith/issues/205"
     :description "Generate images from command's output."}
    {:name "issue-259"
     :url "https://github.com/polyfy/polylith/issues/259"
     :description "Added warning 206 'Unreadable namespace in brick/project' + support for excluding files to parse, see 'poly help check' (warning 206) for details."}
    {:name "issue-264"
     :url "https://github.com/polyfy/polylith/issues/264"
     :description "Improved error messages for workspace.edn and deps.edn config files (now we use clojure.edn when reading edn files)."}
    {:name "issue-272"
     :url "https://github.com/polyfy/polylith/issues/272"
     :description "poly check doesn't check test dependencies (solved by issue 293 and 274)."}
    {:name "issue-274"
     :url "https://github.com/polyfy/polylith/issues/274"
     :description "Support exclusion of bricks to test so that we support both :include and :exclude for projects in workspace.edn."}
    {:name "issue-277"
     :url "https://github.com/polyfy/polylith/issues/277"
     :description "Improved warning 205 by also showing non top namespace files. Error 106 and 108 are not displayed in some edge cases."}
    {:name "issue-282"
     :url "https://github.com/polyfy/polylith/issues/282"
     :description "The check/info/test commands are now reporting unnecessary components (warning 207). If a component is not referred to but still needed, it can be marked as :necessary by that project, see 'poly help check' (warning 207) for details."}
    {:name "issue-286"
     :url "https://github.com/polyfy/polylith/issues/286"
     :description "Make switch-ws available in the shell outside a workspace."}
    {:name "issue-289"
     :url "https://github.com/polyfy/polylith/issues/289"
     :description "Make it possible to show all available options in a shell."}
    {:name "issue-290"
     :url "https://github.com/polyfy/polylith/issues/290"
     :description "Don't allow :commit when creating a workspace within an existing repo."}
    {:name "issue-293"
     :url "https://github.com/polyfy/polylith/issues/293"
     :description "Handle bases that depend on other bases correctly."}
    {:name "issue-294"
     :url "https://github.com/polyfy/polylith/issues/294"
     :description "Support for showing outdated libraries by passing in :outdated to the libs command."}
    {:name "issue-304"
     :url "https://github.com/polyfy/polylith/issues/304"
     :description "Outdated examples/docs (solved by 312)."}
    {:name "issue-309"
     :url "https://github.com/polyfy/polylith/issues/309"
     :description "Improve the performance of the poly commands by optimising the dependency calculations."}
    {:name "issue-312"
     :url "https://github.com/polyfy/polylith/issues/312"
     :description "Build docs/example code should not use build-clj."}
    {:name "issue-318"
     :url "https://github.com/polyfy/polylith/issues/318"
     :description "Use cljdoc for the poly tool documentation."}
    {:name "pr-268"
     :author "https://github.com/seancorfield"
     :url "https://github.com/polyfy/polylith/pull/268"
     :description "Start using tools.deps instead of tools.deps.alpha."}
    {:name "pr-275"
     :author "https://github.com/sundbry"
     :url "https://github.com/polyfy/polylith/pull/275"
     :description "Require clojure.tools.deps correctly in build.clj."}
    {:name "pr-319"
     :author "https://github.com/lread"
     :url "https://github.com/polyfy/polylith/pull/319"
     :description "Tweak bb doc task that offers cljdoc previews + the related PRs 320 and 321."}
    {:name "pr-323"
     :author "https://github.com/john-shaffer"
     :url "https://github.com/polyfy/polylith/pull/323"
     :description "Fix NPE when taking the size of a directory."}
    {:name "pr-324"
     :author "https://github.com/j1mr10rd4n"
     :url "https://github.com/polyfy/polylith/pull/324"
     :description "Fix filename/namespace mismatch in profile.adoc."}]

   :0.2.16-alpha
    [{:name "ws-1.2"
      :attributes [{:attribute "ENTITIES:ENTITY:namespaces:src:NAMESPACE:is-invalid"
                    :description "Added"}]}]
   :0.2.14-alpha
    [{:name "ws-1.1"
      :attributes [{:attribute "settings:vcs:is-git-repo"
                    :description "Added"}
                   {:attribute "projects:PROJECT:is-run-tests"
                    :description "Deleted"}]}]})
