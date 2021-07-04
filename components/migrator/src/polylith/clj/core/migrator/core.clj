(ns polylith.clj.core.migrator.core
  (:require [polylith.clj.core.file.interface :as file]))

; Work in progress

(defn create-brick-config [{:keys []}])

;{:paths ["src"]
; :deps '{me.raynes/fs {:mvn/version "1.4.6"}}
; :aliases {:test {:extra-paths ["test"]
;                  :extra-deps {}}}}


(defn create-brick-configs [{:keys [components bases]}]
  (doseq [brick (concat components bases)]
    (create-brick-config brick)))

(defn create-workspace-config [ws-dir
                               {:keys []
                                {:keys [top-namespace
                                        interface-ns
                                        default-profile-name
                                        compact-views
                                        tag-patterns
                                        projects]} :settings}]
  (file/pretty-spit
    (str ws-dir "/workspace.edn")
    {:top-namespace top-namespace
     :interface-ns interface-ns
     :default-profile-name default-profile-name
     :compact-views compact-views
     :tag-patterns tag-patterns
     :vcs {:name "git", :auto-add false}
     :projects projects}))

;(require '[dev.jocke :as dev])
;(def workspace dev/workspace)

(defn migrate
  "Migrates from the old :toolsdeps1 format, where everything is specified as paths,
   to the new :toolsdeps2 format where bricks have their own 'deps.edn' config file
   and where workspace settings is stored in workspace.edn instead of ./deps.edn.
   0.1.0-alpha9 is the last version that supports the old :toolsdeps1 format."
  [ws-dir workspace]
  (create-workspace-config ws-dir workspace))
