(ns ^:no-doc polylith.clj.core.workspace-clj.profile
  (:require [clojure.string :as str]
            [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.workspace-clj.brick-deps :as brick-deps]))

(defn brick-name [[_ {:keys [local/root]}]]
  (when root
    (cond
      (str/starts-with? root "bases/") (str-util/skip-prefix root "bases/")
      (str/starts-with? root "components/") (str-util/skip-prefix root "components/"))))

(defn brick-name [[_ {:keys [local/root]}] bricks-path]
  (when (and root
             (str/starts-with? root bricks-path))
    [(str-util/skip-prefix root bricks-path)]))

(defn lib? [dep]
  (empty? (concat (brick-name dep "bases/")
                  (brick-name dep "components/"))))

(defn ->brick-paths [{:keys [name type paths]}]
  (map #(str type "s/" name "/" %)
       (concat (:src paths)
               (:test paths))))

(defn brick-libs [name->brick brick-name]
  (let [lib-deps (-> brick-name name->brick :lib-deps)]
    (util/sort-map (map #(deps/convert-dep-to-symbol %)
                        (merge (:src lib-deps)
                               (:test lib-deps))))))

(defn profile [ws-dir [profile-key {:keys [extra-paths extra-deps]}] name->brick user-home]
  (let [;; :extra-paths
        path-entries (extract/from-paths {:src extra-paths} nil)
        path-base-names (vec (sort (select/names path-entries c/base?)))
        path-component-names (vec (sort (select/names path-entries c/component?)))
        project-names (vec (sort (select/names path-entries c/project?)))
        ;; extra-deps
        deps-base-names (mapcat #(brick-name % "bases/") extra-deps)
        deps-component-names (mapcat #(brick-name % "components/") extra-deps)
        deps-brick-names (concat deps-base-names deps-component-names)
        deps-brick-paths (mapcat #(-> % name->brick ->brick-paths) deps-brick-names)
        ;; :local/root deps
        brick-names (brick-deps/extract-brick-names true extra-deps)
        brick-libs (mapcat #(brick-libs name->brick %) brick-names)
        ;; result
        base-names (vec (sort (set (concat path-base-names deps-base-names))))
        component-names (vec (sort (set (concat path-component-names deps-component-names))))
        paths (vec (sort (set (concat extra-paths deps-brick-paths))))
        lib-deps (lib/latest-with-sizes ws-dir nil
                                        (concat brick-libs
                                                (filter lib? extra-deps))
                                        user-home)]
    (util/ordered-map :name (subs (name profile-key) 1)
                      :type "profile"
                      :paths paths
                      :lib-deps lib-deps
                      :component-names component-names
                      :base-names base-names
                      :project-names project-names)))

(defn profile? [[alias]]
  (str/starts-with? (name alias) "+"))

(defn sorter [default-profile-name]
  (partial sort-by #(if (= (:name %) default-profile-name) "-" (:name %))))

(defn profiles [ws-dir default-profile-name aliases name->brick user-home]
  (vec ((sorter default-profile-name)
        (map #(profile ws-dir % name->brick user-home)
             (filterv profile? aliases)))))

(comment
  (def ws-dir "examples/doc-example")
  (def aliases '{:dev {:extra-paths ["development/src"], :extra-deps {poly/cli #:local{:root "bases/cli"}, poly/user-api #:local{:root "bases/user-api"}, org.clojure/clojure #:mvn{:version "1.11.1"}, org.apache.logging.log4j/log4j-core #:mvn{:version "2.13.3"}, org.apache.logging.log4j/log4j-slf4j-impl #:mvn{:version "2.13.3"}}}, :test {:extra-paths ["bases/cli/test" "bases/user-api/test" "projects/command-line/test"]}, :+default {:extra-deps #:poly{user #:local{:root "components/user"}}, :extra-paths ["components/user/test"]}, :+remote {:extra-deps #:poly{user-remote #:local{:root "components/user-remote"}}, :extra-paths ["components/user-remote/test"]}, :build {:deps {org.clojure/tools.deps #:mvn{:version "0.16.1281"}, io.github.clojure/tools.build #:mvn{:version "0.9.5"}, io.github.seancorfield/build-uber-log4j2-handler #:git{:tag "v0.1.5", :sha "55fb6f6"}}, :paths ["build/resources"], :ns-default build}, :poly {:main-opts ["-m" "polylith.clj.core.poly-cli.core"], :extra-deps #:polylith{clj-poly #:mvn{:version "0.2.18"}}}})
  (def name->brick {"user" {:name "user", :type "component", :paths {:src ["src" "resources"], :test ["test"]}, :namespaces {:src [{:name "interface", :namespace "se.example.user.interface", :file-path "components/user/src/se/example/user/interface.clj", :imports ["se.example.user.core"]} {:name "core", :namespace "se.example.user.core", :file-path "components/user/src/se/example/user/core.clj", :imports []}], :test [{:name "interface-test", :namespace "se.example.user.interface-test", :file-path "components/user/test/se/example/user/interface_test.clj", :imports ["clojure.test" "se.example.user.interface"]}]}, :lib-deps {}, :interface {:name "user", :definitions [{:name "hello", :type "function", :arglist [{:name "name"}]}]}}, "user-remote" {:name "user-remote", :type "component", :paths {:src ["src" "resources"], :test ["test"]}, :namespaces {:src [{:name "interface", :namespace "se.example.user.interface", :file-path "components/user-remote/src/se/example/user/interface.clj", :imports ["se.example.user.core"]} {:name "core", :namespace "se.example.user.core", :file-path "components/user-remote/src/se/example/user/core.clj", :imports ["slacker.client"]}], :test [{:name "interface-test", :namespace "se.example.user.interface-test", :file-path "components/user-remote/test/se/example/user/interface_test.clj", :imports ["clojure.test" "se.example.user.interface"]}]}, :lib-deps {:src {"compojure/compojure" {:version "1.6.2", :type "maven", :size 15172}, "http-kit/http-kit" {:version "2.4.0", :type "maven", :size 191467}, "ring/ring" {:version "1.8.1", :type "maven", :size 4621}, "slacker/slacker" {:version "0.17.0", :type "maven", :size 28408}}}, :interface {:name "user", :definitions [{:name "hello", :type "function", :arglist [{:name "name"}]}]}}, "cli" {:name "cli", :type "base", :paths {:src ["src" "resources"], :test ["test"]}, :namespaces {:src [{:name "core", :namespace "se.example.cli.core", :file-path "bases/cli/src/se/example/cli/core.clj", :imports ["se.example.user.interface"]}], :test [{:name "core-test", :namespace "se.example.cli.core-test", :file-path "bases/cli/test/se/example/cli/core_test.clj", :imports ["clojure.test" "se.example.cli.core"]}]}, :lib-deps {}}, "user-api" {:name "user-api", :type "base", :paths {:src ["src" "resources"], :test ["test"]}, :namespaces {:src [{:name "core", :namespace "se.example.user-api.core", :file-path "bases/user-api/src/se/example/user_api/core.clj", :imports ["se.example.user-api.api" "slacker.server"]} {:name "api", :namespace "se.example.user-api.api", :file-path "bases/user-api/src/se/example/user_api/api.clj", :imports ["se.example.user.interface"]}], :test [{:name "core-test", :namespace "se.example.user-api.core-test", :file-path "bases/user-api/test/se/example/user_api/core_test.clj", :imports ["clojure.test" "se.example.user-api.core"]}]}, :lib-deps {:src {"slacker/slacker" {:version "0.17.0", :type "maven", :size 28408}}}}})
  (def user-home "/Users/joakimtengstrand")

  (profiles ws-dir "default" aliases name->brick user-home)
  #__)

(defn active-profiles [{:keys [selected-profiles]}
                       default-profile-name
                       profiles]
  (if (empty? selected-profiles)
    (if (empty? profiles)
      #{}
      #{default-profile-name})
    (if (contains? (set selected-profiles) "")
      []
      (set selected-profiles))))
