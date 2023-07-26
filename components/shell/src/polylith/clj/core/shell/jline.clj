(ns ^:no-doc polylith.clj.core.shell.jline
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.shell.candidate.engine :as engine])
  (:import [org.jline.terminal TerminalBuilder]
           [org.jline.reader Completer]
           [org.jline.reader.impl DefaultParser]
           [org.jline.reader.impl DefaultParser$ArgumentList]
           [org.jline.reader Candidate]
           [org.jline.reader LineReader]
           [org.jline.reader LineReaderBuilder]
           [org.jline.reader Parser]
           [org.jline.reader Parser$ParseContext]
           [org.jline.reader ParsedLine]
           [org.jline.reader LineReader$SuggestionType]))

(defn split-word [word]
  (if (str/starts-with? word ":")
    (if (str/starts-with? ":project" word)
      [word]
      [(subs word 1)])
    (let [splitted (str/split word #"[:/]")]
      (if (or (str/ends-with? word ":")
              (str/ends-with? word "/"))
        (conj splitted "")
        splitted))))

(defn append-words [{:keys [words split]} word]
  (let [sub-words (split-word word)
        split-separator (when split [split])
        new-words (vec (concat words split-separator sub-words))]
    {:split       :next
     :words       (vec new-words)
     :word-index  (dec (count new-words))
     :word-cursor (-> sub-words last count)}))

(defn default-parser [line words word-index word-cursor cursor]
  (DefaultParser$ArgumentList.
    (DefaultParser.)
    line
    words
    word-index
    word-cursor
    cursor))

(def parser (proxy [Parser] []
              (parse [^String line
                      ^Integer _
                      ^Parser$ParseContext _]
                (let [{:keys [words word-index word-cursor]} (reduce append-words {:words []}
                                                                     (str-util/split-text line))
                      cursor (count line)
                      result (default-parser line words word-index word-cursor cursor)]
                  result))
              (isEscapeChar [^Character _]
                false)))

(defn candidate [{:keys [value display description stay?]}]
  (let [last-char (-> value str/reverse first str)
        suffix (when (contains? #{":" "/"} last-char)
                 last-char)]
    (if stay?
      (Candidate. value display nil description suffix nil false)
      (Candidate. value display nil description nil nil true))))

(defn ->completer []
  (proxy [Completer] []
    (complete [^LineReader _
               ^ParsedLine parsed-line
               ^java.util.List candidates]
      (let [;line (.line parsed-line)
            words (vec (.words parsed-line))]
        (.addAll candidates (map candidate
                                 (engine/candidates words)))))))

(defn reader []
  (let [terminal (-> (TerminalBuilder/builder)
                     (.system true)
                     (.dumb true)
                     (.build))
        completer (->completer)
        result (-> (LineReaderBuilder/builder)
                   (.terminal terminal)
                   (.parser parser)
                   (.completer completer)
                   (.build))]
    (.setAutosuggestion result LineReader$SuggestionType/COMPLETER)
    result))
