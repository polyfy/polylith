(ns polylith.clj.core.ws-file.from-0-to-1
  (:require [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.version.interface :as ver])
  (:refer-clojure :exclude [alias]))

(defn convert-version [{:keys [version ws-schema-version]}]
  (let [breaking (:breaking ws-schema-version 0)
        non-breaking (:non-breaking ws-schema-version 0)
        from-version {:ws {:type :toolsdeps1
                           :breaking breaking
                           :non-breaking non-breaking}
                      :release-name version}]
    (ver/version from-version)))

(defn alias [[project-name alias]]
  [project-name {:alias alias}])

(defn tag-patterns [stable-tag-pattern release-tag-pattern]
  {:stable stable-tag-pattern
   :release release-tag-pattern})

(defn convert-vcs [vcs]
  {:name vcs
   :auto-add false
   :polylith {:repo git/repo, :branch git/branch}})

(defn convert-settings [{:keys [vcs
                                project-to-alias
                                stable-tag-pattern
                                release-tag-pattern
                                thousand-sep
                                empty-char] :as settings}]
  (assoc (dissoc settings :ns-to-lib
                          :project-to-alias
                          :stable-tag-pattern
                          :release-tag-pattern
                          :thousand-sep
                          :empty-char
                          :version
                          :ws-schema-version)
         :vcs (convert-vcs vcs)
         :tag-patterns (tag-patterns stable-tag-pattern release-tag-pattern)
         :projects (into {} (map alias project-to-alias))
         :thousand-separator thousand-sep
         :empty-character empty-char))

(defn lib-dep [name]
  [name {}])

(defn lib-deps [names]
  (into {} (map lib-dep names)))

(defn convert-brick [{:keys [lib-dep-names
                             lib-imports-src
                             lib-imports-test
                             lines-of-code-src
                             lines-of-code-test
                             namespaces-src
                             namespaces-test] :as component}]
  (assoc (dissoc component :lib-dep-names
                           :lib-imports-src
                           :lib-imports-test
                           :lines-of-code-src
                           :lines-of-code-test
                           :namespaces-src
                           :namespaces-test)
         :lib-deps {:src (lib-deps lib-dep-names)}
         :lib-imports {:src lib-imports-src
                       :test lib-imports-test}
         :lines-of-code {:src lines-of-code-src
                         :test lines-of-code-test}
         :namespaces {:src namespaces-src
                      :test namespaces-test}))

(defn convert-unmerged [{:keys [src-paths test-paths lib-deps test-lib-deps] :as unmerged}]
  (when unmerged
    {:paths {:src src-paths
             :test test-paths}
     :lib-deps {:src lib-deps
                :test test-lib-deps}}))

(defn convert-project [{:keys [config-file
                               base-names
                               deps
                               test-base-names
                               component-names
                               test-component-names
                               lines-of-code-src
                               lines-of-code-test
                               total-lines-of-code-src
                               total-lines-of-code-test
                               lib-imports
                               lib-imports-test
                               lib-deps
                               test-lib-deps
                               src-paths
                               test-paths
                               namespaces-src
                               namespaces-test
                               unmerged] :as project}]
  (cond-> (assoc (dissoc project :config-file
                                 :test-base-names
                                 :test-component-names
                                 :test-lib-deps
                                 :lib-imports-test
                                 :lines-of-code-src
                                 :lines-of-code-test
                                 :total-lines-of-code-src
                                 :total-lines-of-code-test
                                 :namespaces-src
                                 :namespaces-test
                                 :src-paths
                                 :test-paths
                                 :unmerged)
                 :base-names {:src base-names
                              :test test-base-names}
                 :component-names {:src component-names
                                   :test test-component-names}
                 :config-filename config-file
                 :deps {:src deps}
                 :lib-deps {:src lib-deps
                            :test test-lib-deps}
                 :lib-imports {:src lib-imports
                               :test lib-imports-test}
                 :lines-of-code {:src lines-of-code-src
                                 :test lines-of-code-test
                                 :total {:src total-lines-of-code-src
                                         :test total-lines-of-code-test}}
                 :namespaces {:src namespaces-src
                              :test namespaces-test}
                 :paths {:src src-paths
                         :test test-paths})
          unmerged (assoc :unmerged (convert-unmerged unmerged))))

(defn convert-changes [{:keys [git-command] :as changes}]
  (assoc (dissoc changes :git-command) :git-diff-command git-command))

(defn convert
  "Convert from the old workspace format, sed by version 0.2.0-alpha9
   and earlier, stored as type :toolsdeps1, to the new format."
  [{:keys [settings bases components projects changes] :as ws}]
  (assoc ws :settings (convert-settings settings)
            :bases (mapv convert-brick bases)
            :components (mapv convert-brick components)
            :projects (mapv convert-project projects)
            :changes (convert-changes changes)
            :version (convert-version settings)))
