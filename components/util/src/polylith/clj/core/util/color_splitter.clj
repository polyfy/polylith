(ns polylith.clj.core.util.color-splitter
  (:require [clojure.string :as str]
            [polylith.clj.core.util.colors :as c]))

(defn color-index [[color code] text x font-width]
  (let [index (str/index-of text code)
        start-text (cond
                     (nil? index) text
                     (zero? index) ""
                     :else (subs text 0 index))]
    (if index
      {:index index
       :color (if (= :reset color) :white color)
       :text start-text
       :next-text (subs text (+ index (count code)))
       :new-x (+ x (* (count start-text) font-width))}
      {:index 9999
       :color :white
       :text start-text
       :next-text ""
       :new-x x})))

(defn split-colors [message x font-width]
  (loop [result []
         text message
         next-color :white
         x x]
    (if (= "" text)
      result
      (let [{:keys [color text next-text new-x]} (apply min-key :index (map #(color-index % text x font-width) c/color->code))
            new-result (conj result {:color next-color :text text :x x})]
        (recur new-result next-text color new-x)))))
