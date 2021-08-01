(ns polylith.clj.core.user-input.params
  (:require [clojure.string :as str]))

(defn named? [arg]
  (and (-> arg nil? not)
       (str/includes? arg ":")))

(defn unnamed? [arg]
  (and (-> arg nil? not)
       (-> arg named? not)))

(defn join-quotes [all-vals]
  (let [val (first all-vals)
        vals (rest all-vals)]
    (when val
      (if (str/starts-with? val "\"")
        (let [i (ffirst (filterv #(-> % second (str/ends-with? "\""))
                                 (map-indexed vector all-vals)))]
          (cond
            (nil? i) []
            (zero? i) (cons (subs val 1 (dec (count val)))
                            (join-quotes vals))
            :else (cons (str/join ":" (concat [(subs val 1)]
                                              (take (dec i) vals)
                                              [(str/join (drop-last (first (drop (dec i) vals))))]))

                        (join-quotes (drop i vals)))))
        (cons val (join-quotes vals))))))

(defn key-name [arg single-arg-commands]
  (let [parts (join-quotes (str/split arg #":"))
        n#parts (count parts)
        keyname (-> parts first keyword)]
    (cond
      (= "" (first parts)) [(keyword (str (second parts) "!")) "true"]
      (= 1 n#parts) [keyname ""]
      (contains? single-arg-commands (first parts)) [keyname (str/join ":" (rest parts))]
      (= 2 n#parts) [keyname (second parts)]
      :else [keyname (vec (rest parts))])))

(defn extract [args single-arg-commands]
  (let [unnamed-args (filterv unnamed? args)
        named-args (into {} (map #(key-name % single-arg-commands)
                                 (filterv named? args)))]
    {:named-args named-args
     :unnamed-args unnamed-args}))
