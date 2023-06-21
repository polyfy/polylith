;; Hacks here

;; Hack 1: Load tools.deps pod instead of library

(require '[babashka.pods :as pods])

(let [pod-binary (-> (clojure.java.io/file *file*) .getParentFile .getParentFile .getParentFile (clojure.java.io/file "tools-deps-native") .getAbsolutePath)]
  ;; FIXME fetch pod in some other way
  (pods/load-pod pod-binary))

;; Hack 2: add clojure.tools.reader/resolve-symbol

(defn ^:private ns-name* [x]
  (if (instance? clojure.lang.Namespace x)
    (name (ns-name x))
    (name x)))
    
(defn- resolve-alias [sym]
  ((or ;*alias-map*
       (ns-aliases *ns*)) sym))

(defn- resolve-ns [sym]
  (or (resolve-alias sym)
      (find-ns sym)))

(defn ^:dynamic resolve-symbol
  "Resolve a symbol s into its fully qualified namespace version"
  [s]
  (if (pos? (.indexOf (name s) "."))
    (if (.endsWith (name s) ".")
      (let [csym (symbol (subs (name s) 0 (dec (count (name s)))))]
        (symbol (str (name (resolve-symbol csym)) ".")))
      s)
    (if-let [ns-str (namespace s)]
      (let [ns (resolve-ns (symbol ns-str))]
        (if (or (nil? ns)
                (= (ns-name* ns) ns-str)) ;; not an alias
          s
          (symbol (ns-name* ns) (name s))))
      (if-let [o ((ns-map *ns*) s)]
        (if (class? o)
          (symbol (.getName ^Class o))
          (if (var? o)
            (symbol o) #_(symbol (->  o .ns ns-name*) (-> o .sym name))))
        (symbol (ns-name* *ns*) (name s))))))

(intern 'clojure.tools.reader 'resolve-symbol resolve-symbol)

;; Hack 3: prevent use of GenericVersionScheme here https://github.com/polyfy/polylith/blob/1209a81e6b8f70987050d65d106e99d1a902969a/components/lib/src/polylith/clj/core/lib/maven_dep.clj#L4

(ns polylith.clj.core.lib.maven-dep)

(defn version [ver]
  (when ver
    ver #_(.toString (.parseVersion ^GenericVersionScheme version-scheme ver))))

(defn latest
  "Return the latest Maven library version."
  [coord1 coord2 mvn-key]
  (let [v1 (version (mvn-key coord1))
        v2 (version (mvn-key coord2))]
    (if (and v1 v2)
      (if (< (compare v1 v2) 0)
        coord2 coord1)
      (if v1 coord1 coord2))))

;; Hack 4: Unable to resolve classname: org.apache.maven.model.Model

(ns polylith.clj.core.antq.ifc)

(defn library->latest-version [_] {})


;; Hack 5: Disable shell: ^--- Unable to resolve classname: org.jline.terminal.TerminalBuilder
(ns polylith.clj.core.shell.interface)

(defn start [execute user-input workspace-fn workspace color-mode]
  (throw (ex-info "Unsupported" {})))

(ns polylith-bb.core
  (:require [polylith.clj.core.poly-cli.core :as core]))
  
(defn -main [& args]
  (apply core/-main args))


