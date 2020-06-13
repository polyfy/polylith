(ns polylith.common.extract-aliases
  (:require [clojure.string :as str]))

(defn test? [env]
  (if env
    (str/ends-with? env "-test")
    false))

(defn include? [[key] include-test?]
  (and (= "env" (namespace key))
       (if include-test?
         true
         (-> key test? not))))

(defn remove-test-prefix [env]
  (when env
    (if (str/ends-with? env "-test")
      (subs env 0 (- (count env) 5))
      env)))

(defn matched? [[key] src-env]
  (if src-env
    (and (= src-env
            (remove-test-prefix (name key))))
    true))

(defn extract
  ([deps env]
   (extract deps env false))
  ([deps env include-tests?]
   (let [src-env (remove-test-prefix env)
         aliases (filter #(include? % include-tests?)
                         (:aliases deps))]
     (filterv #(matched? % src-env) aliases))))
