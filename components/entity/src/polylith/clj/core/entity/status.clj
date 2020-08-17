(ns polylith.clj.core.entity.status
  (:require [polylith.clj.core.entity.matchers :as m]))

(defn status [path-entries name & criterias]
  (let [statuses (concat [m/=exists (m/=name name)] criterias)
        status-x? (m/filter-entries path-entries (conj statuses m/=standard))
        status-+? (m/filter-entries path-entries (conj statuses m/=profile))]
    (if (-> status-x? empty? not)
      "x"
      (if (-> status-+? empty? not) "+" "-"))))

(defn src-status-flag [path-entries category name]
  (status path-entries name m/=src m/=src-path category))

(defn resources-status-flag [path-entries category name show-resources?]
  (if show-resources?
    (status path-entries name m/=src m/=resources-path category)
    ""))

(defn test-status-flag [path-entries category name]
  (status path-entries name m/=test m/=test-path category))

(def entity->category {:brick m/=brick
                       :env m/=environment})

(defn status-flags [path-entries entity name show-resources?]
  (let [category (entity->category entity)]
    (str (src-status-flag path-entries category name)
         (resources-status-flag path-entries category name show-resources?)
         (test-status-flag path-entries category name))))
