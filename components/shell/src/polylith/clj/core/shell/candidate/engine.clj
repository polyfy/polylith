(ns polylith.clj.core.shell.candidate.engine
  (:require [clojure.string :as str]
            [polylith.clj.core.shell.candidate.shared :as shared]
            [polylith.clj.core.shell.candidate.specification :as specification])
  (:refer-clojure :exclude [next]))

(def ws (atom nil))
(def groups (atom nil))

(comment
  (def workspace (-> "components/test-helper/resources/workspace.edn"
                     slurp read-string))

  (require '[dev.jocke :as dev])
  (reset! ws workspace)
  (reset! groups specification/groups)
  #__)

(defn clean-words [value]
  (str/replace (str value) "\"" "'"))

(defn tap [fn-name word candidate]
  (tap> {:fn    fn-name
         :word  word
         :candidate candidate}))

(defn spec-candidates []
  (if @ws
    (specification/candidates @ws)
    specification/candidates-outside-ws-root))

(defn first-candidate []
  {:type :candidates
   :candidates (spec-candidates)})

(defn filter-exact-match [word candidates]
  (filter #(= word (:parsed-value %)) candidates))

(defn starts-with [{:keys [parsed-value]} word]
  (and (not (nil? parsed-value))
     (str/starts-with? parsed-value word)))

(defn filter-candidates [word candidates potential-exact-match?]
  (let [potentials (filter-exact-match word candidates)
        filtered (filterv #(starts-with % word) candidates)
        ordered (sort-by :order (filter :order filtered))
        order (-> ordered first :order)]
    (if (and potential-exact-match?
             (= 1 (count potentials)))
      potentials
      (if (empty? ordered)
        filtered
        (vec (take-while #(= order (:order %))
                         ordered))))))

(defn empty-if-nil [candidates]
  (or candidates #{}))

(defn set-group-arg! [{:keys [group] :as candidate} word]
  (let [{:keys [id param]} group]
    (when param
      (let [[param word] (if (= :flag param)
                           [word true]
                           [param word])
            args (get-in @groups [id param :args])]
        (reset! groups
                (assoc-in @groups [id param :args]
                          (if word
                            (if args
                              (conj args word)
                              [word])
                            (if args
                              args
                              []))))))
    candidate))

(defn next [{:keys [word group] :as candidate}]
  (if word
    (assoc candidate :type :candidates
                     :candidates [])
    (if-let [id (:id group)]
      (assoc candidate :type :candidates
                       :candidates (mapv second
                                         (filterv #(-> % second :args not)
                                                  (-> groups deref id))))
      (assoc candidate :type :candidates))))

(defn group-candidates [candidate]
  (mapv second
        (filterv #(-> % second :args not)
                 ((deref groups) (-> candidate :group :id)))))

(defn select [{:keys [type function candidates] :as candidate}
              word potential-exact-match?]
  (let [next-candidates (case type
                          :candidates candidates
                          :remaining (group-candidates candidate)
                          :fn ((var-get function) candidate @groups @ws))
        arg-value? (= "" (-> next-candidates first :parsed-value))
        filtered-candidates (if arg-value?
                              next-candidates
                              (filter-candidates word next-candidates potential-exact-match?))
        exact-match? (and (= 1 (count filtered-candidates))
                          (or arg-value?
                              (= word (-> filtered-candidates first :parsed-value))))]
    (if exact-match?
      (let [{:keys [child] :as match} (first filtered-candidates)]
        (if child
          child
          (set-group-arg! match word)))
      (do
        (when potential-exact-match? (set-group-arg! candidate word))
        (assoc candidate
               :candidates filtered-candidates
               :word word)))))

(defn select-candidates [candidate [word potential-exact-match?]]
  (tap "select-candidates" word candidate)
  (if (= :next word)
    (next candidate)
    (select candidate word potential-exact-match?)))

(defn reset-groups []
  (reset! groups
          (shared/groups (spec-candidates))))

(defn potential-exact-match [word next-word]
  [word (and (not= :next word)
             (not= :end next-word))])

(defn with-potential-exact-match [words]
  (mapv potential-exact-match words (conj (vec (drop 1 words)) :end)))

(defn select-candidate [words]
  (reset-groups)
  (reduce select-candidates
          (first-candidate)
          (with-potential-exact-match words)))

(defn candidates [line words]
  (tap> {:fn "----- candidates -----"
         :line line
         :first-candidate (first-candidate)
         :words-str (clean-words words)
         :words words
         :groups @groups
         :workspace @ws})
  (let [result (-> (select-candidate words)
                   :candidates
                   empty-if-nil)]
    (tap> {:candidates result
           :groups @groups})
    result))

(comment
  (reset! ws nil)
  (reset! ws (-> "components/test-helper/resources/workspace.edn"
                 slurp read-string))

  (reset-groups)
  (deref groups)

  (with-potential-exact-match ["deps" :next "brick" "deployer" :next ""])

  (select-candidate ["ws" :next "get" "components" "shell" "interface" ""])
  (select-candidate ["ws" :next "get" "components" "shell" "interface"])
  (select-candidate ["ws" :next "get" "components" "shell" ""])

  (with-potential-exact-match ["deps" :next "brick" "deployer" :next "project" ""])


  (select-candidate ["ws" :next "out" "components" :next ""])

  ["ws" :next "out" ".." ""]
  ["ws" :next "ws-file" ".." "usermanager-example" "ws.edn" :next ""]

  ;(with-potential-exact-match ["deps" :next "brick" "deployer" :next ""])
  ;[["deps" true] [:next false] ["brick" true] ["deployer" true] [:next false] ["" false]]
  (def step1 (select-candidates (first-candidate) ["deps" true]))
  (def step2 (select-candidates step1 [:next false]))
  (def step3 (select-candidates step2 ["brick" true]))
  (def step4 (select-candidates step3 ["deployer" true]))
  (def step5 (select-candidates step4 [:next false]))
  (def step6 (select-candidates step5 ["project" true]))
  (def step7 (select-candidates step6 ["" false])))
