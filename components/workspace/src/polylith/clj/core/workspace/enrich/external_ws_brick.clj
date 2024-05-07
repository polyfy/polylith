(ns ^:no-doc polylith.clj.core.workspace.enrich.external-ws-brick
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]))

(defn brick [{:keys [alias interface name type]}]
  {:brick (cond-> {:alias alias
                   :type type
                   :name name}
                  interface (assoc :interface interface))})

(defn interface-name [{:keys [components]} component-name]
  (-> (util/find-first #(= component-name (:name %)) components)
      :interface :name))

(defn brick-lib [[lib-key {:keys [path]}]
                 dir->alias
                 alias->workspace]
  (when path
    (let [[suffixed-path alias] (first (filter #(str/starts-with? path (first %))
                                               dir->alias))
          brick-path (subs path (count suffixed-path))
          [type brick-name] (cond (str/starts-with? brick-path "bases/") [:base (subs brick-path 6)]
                                  (str/starts-with? brick-path "components/") [:component (subs brick-path 11)])
          workspace (alias->workspace alias)
          interface (interface-name workspace brick-name)]
      (when (and alias type)
        {:lib-key lib-key
         :alias alias
         :type type
         :interface interface
         :name brick-name
         :path path}))))

(defn full-name [{:keys [alias name]}]
  (str alias "/" name))

(defn convert-libs-to-bricks
  "When we read all workspaces from disk, we treat all :local/root as libraries.
   This function adds the :brick key to libraries that refer bricks in other workspaces."
  [lib-deps configs workspaces]
  (let [alias->workspace (into {} (map (juxt :alias identity) workspaces))
        dir->alias (into {} (map (juxt #(str (:dir %) "/") :alias) (-> configs :workspace :workspaces)))
        brick-libs (filter identity
                           (map #(brick-lib % dir->alias alias->workspace)
                                lib-deps))
        base-names (vec (sort (mapv full-name
                                    (filter #(= :base (:type %))
                                            brick-libs))))
        component-names (vec (sort (mapv full-name
                                         (filter #(= :component (:type %))
                                                 brick-libs))))
        ws-bricks (into {} (map (juxt :lib-key brick)
                                brick-libs))
        lib-deps-with-ws-bricks (merge-with merge lib-deps ws-bricks)]
    [base-names
     component-names
     lib-deps-with-ws-bricks]))

(comment
  (def lib-deps {"shared/util" {:local/root "../shared/components/util", :type "local", :path "../shared/components/util", :size 5068},
                 "org.clojure/clojure" {:version "1.11.1", :type "maven", :size 4105111},
                 "shared/share-me" {:local/root "../shared/share-me", :type "local", :path "../shared/share-me"}})
  (def configs '{:components [{:deps {:paths ["src"], :deps {}, :aliases {:test {:extra-paths ["test"], :extra-deps {}}}}, :name "hello", :type "component"} {:deps {:paths ["src"], :deps {}, :aliases {:test {:extra-paths [], :extra-deps {}}}}, :name "howdy", :type "component"} {:deps {:paths ["src"], :deps {}, :aliases {:test {:extra-paths [], :extra-deps {}}}}, :name "math", :type "component"}], :bases [{:deps {:paths ["src"], :deps {}, :aliases {:test {:extra-paths [], :extra-deps {}}}}, :name "cli", :type "base"}], :projects [{:deps {:aliases {:dev {:extra-paths ["development/src"], :extra-deps {backend/stuff #:local{:root "components/math"}, backend/cli #:local{:root "bases/cli"}, shared/util #:local{:root "../shared/components/util"}, org.clojure/clojure #:mvn{:version "1.11.1"}}}, :test {:extra-paths []}, :+default {:extra-deps {backend/hello #:local{:root "components/hello"}, shared/share-me #:local{:root "../shared/share-me"}}}, :+howdy {:extra-deps #:backend{howdy #:local{:root "components/howdy"}}}, :poly {:main-opts ["-m" "polylith.clj.core.poly-cli.core"], :extra-deps #:polylith{clj-poly #:local{:root "../../../projects/poly"}}}}}, :name "development", :type "project"} {:deps {:deps {backend/stuff #:local{:root "../../components/math"}, backend/cli #:local{:root "../../bases/cli"}, backend/hello #:local{:root "../../components/howdy"}, shared/util #:local{:root "../../../shared/components/util"}, org.clojure/clojure #:mvn{:version "1.11.1"}}, :aliases {:test {:extra-paths [], :extra-deps {}}}}, :name "system", :type "project"}], :user {:color-mode "dark", :empty-character ".", :thousand-separator ",", :ws-shortcuts {:root-dir "/Users/joakimtengstrand/source/polylith", :paths [{:dir "examples/doc-example"} {:dir "examples/for-test"} {:dir "examples/local-dep"} {:dir "examples/local-dep-old-format"} {:dir "examples/missing-component"} {:dir "examples/multiple-workspaces2/backend"} {:dir "examples/multiple-workspaces2/shared"} {:dir "examples/multiple-workspaces2/shared2"} {:dir "examples/profiles"} {:dir "examples/poly-rcf"} {:dir "examples/poly-rcf/monolith", :name "monolith-poly-rcf"} {:dir "examples/test-runners"} {:dir "../clojure-polylith-realworld-example-app", :name "realworld"} {:file "../sandbox/oliver.edn"}]}}, :workspace {:top-namespace "backend", :interface-ns "interface", :default-profile-name "default", :compact-views #{}, :vcs {:name "git", :auto-add false}, :tag-patterns {:stable "stable-*", :release "v[0-9]*"}, :projects {"development" {:alias "dev"}, "system" {:alias "sys"}}, :workspaces [{:alias "s", :dir "../shared"}]}})

  (def workspace dev.jocke/workspace)
  (def workspaces (:workspaces workspace))
  (def ws (first workspaces))



  (interface-name (alias->workspace "s") "util")



  (def components (:components ws))

  (last (convert-libs-to-bricks lib-deps configs workspaces))

  #__)



