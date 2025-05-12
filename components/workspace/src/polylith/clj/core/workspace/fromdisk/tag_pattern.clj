(ns ^:no-doc polylith.clj.core.workspace.fromdisk.tag-pattern)

(defn patterns [tag-patterns stable-tag-pattern release-tag-pattern]
  (merge tag-patterns
         {:stable (or (:stable tag-patterns)
                      stable-tag-pattern
                      "^stable-*")
          :release (or (:release tag-patterns)
                       release-tag-pattern
                       "^v[0-9]*")}))
