(ns ^:no-doc polylith.clj.core.shell.candidate.creators
  (:require [polylith.clj.core.util.interface.color :as color]))

(defn with-val [candidate value]
  (cond
    (map? value) (merge candidate value)
    (keyword? value) (assoc candidate :top-group-id value)
    (string? value) (assoc candidate :parsed-value value)
    (integer? value) (assoc candidate :order value)
    (boolean? value) (assoc candidate :stay? value)
    (sequential? value) (assoc candidate :candidates (vec value))
    :else (throw (Exception. (str "Unknown type for value: " value)))))

(defn candidate
  "Creates a candidate which is later used by the jline library.

    value:         The value that will be inserted at the cursor in the shell,
                   e.g. when a value is selected from the list of candidates
                   below the cursor (the value may be displayed as e.g.
                   'projects' whereas the value is stored as 'projects:'.

    display:       The value that is displayed at the row below the cursor.

    parsed-value:  The value returned by the parse. Endings like : and / are
                   removed by the parser, e.g. name: becomes name. Used when
                   comparing values against the parsed values.

    type:          :candidates = Used if the value is stored directly in this candidate
                                 (:value, :display, and :parsed-value).
                   :fn = Used in combination with :select to calculate the candidates by
                         calling the given :function with the current candidate,
                         workspace and groups as parameters.
                   :remaining = Used when we want to pick the next remaining value from
                                the group. The values for a group are stored in the
                                'groups' atom, and stored in the key
                                'group key' > 'param name' > :args, as a vector.
    group:         Contains a map with the keys :id and :param. The :id key says which
                   group this candidate belongs to and is used to switch to the next
                   remaining group when we encounter a :next word, but also to set the
                   :args value in the @groups atom for the group :id and :param if :param
                   is given, which is later used to calculate the remaining candidates
                   in the case :type is set to :remaining.

    order:         If set, then the candidate with the lowest number will be picked.
                   Used in combination with the :group attribute. E.g. if two
                   candidates has order set to 1 and 2 respectively, then the candidate
                   with order 1 will be suggested first and the other value as second.

    stay?          Set to true if the cursor should stay with the current word. Used
                   with e.g. values like name: so that a name can be given. If set to
                   false, then the cursor will move to the next word by adding a space.

    candidates     If type is :candidates then this attribute may be set. Lists all the
                   next possible candidates."
  [value display parsed-value type args]
  (merge {:value value
          :display display
          :parsed-value parsed-value
          :type type
          :candidates []}
         (reduce with-val {} args)))

(defn optional []
  {:description "optional"})

(defn group [id]
  {:group {:id id}})

(defn in-group [group-id candidate]
  (merge candidate {:group {:id group-id
                            :param (:parsed-value candidate)}}))

(defn function [f]
  {:function f})

(defn single-txt
  ([value & args]
   (candidate value value value :candidates (conj args false))))

(defn flag-explicit
  ([value group-id & values]
   (let [flag (str ":" value)]
     (candidate flag flag flag :remaining (concat values [false {:group {:id group-id
                                                                         :param flag}}])))))

(defn flag
  ([value group-id & values]
   (let [flag (str ":" value)]
     (candidate flag flag value :remaining (concat values [false {:group {:id group-id
                                                                          :param value}}])))))

(defn group-arg [value group-id param & values]
  (candidate value value value :remaining (concat values [{:group {:id group-id
                                                                   :param param}}])))

(defn multi-arg [group-id param & values]
  (candidate "" "" "" :remaining (concat values [{:group {:id group-id
                                                          :param param}}])))

(defn fn-comma-arg [value group-id param function & values]
  (candidate (str value ":") value value :fn (concat values [{:function function
                                                              :group {:id group-id
                                                                      :param param}}])))

(defn fn-explorer [value group-id select-fn & values]
  (candidate (str value ":") value value :fn (concat values [true
                                                             {:group {:id group-id}
                                                              :function select-fn}
                                                             {:child {:type :fn
                                                                      :stay? true
                                                                      :group {:id group-id
                                                                              :param value}
                                                                      :function select-fn}}])))


(defn fn-values [value group-id select-fn & extra-values]
  (candidate (str value ":") value value :fn (concat extra-values
                                                     [true
                                                      {:group {:id group-id}
                                                       :function select-fn}])))
(defn fn-explorer-child-plain [value entity color-mode stay? group select-fn]
  (candidate value
             (color/entity entity value color-mode)
             value :fn [true
                        {:type :fn
                         :stay? stay?
                         :group group
                         :function select-fn}]))

(defn fn-explorer-child [value entity color-mode stay? group select-fn]
  (candidate (str value ":")
             (color/entity entity value color-mode)
             value :fn [true
                        {:type :fn
                         :stay? stay?
                         :group group
                         :function select-fn}]))

(defn multi-param
  ([value & args]
   (candidate (str value ":") value value :candidates (concat args [true]))))
