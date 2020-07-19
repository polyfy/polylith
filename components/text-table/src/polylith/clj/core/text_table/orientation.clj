(ns polylith.clj.core.text-table.orientation
  (:require [clojure.core.matrix :as m]))

(defn height [header orientation]
  (if (= :horizontal orientation)
    1
    (count header)))

(defn with-header-content [matrix [y x c indent]]
  (assoc-in matrix [y (+ x indent)] c))

(defn with-header [matrix [y n#rows orientation header]]
  (if (= :vertical orientation)
    (reduce with-header-content matrix
            (mapv vector
                  (repeat y)
                  (range)
                  (map identity header)
                  (repeat (- n#rows (count header)))))
    (with-header-content matrix [y 0 header (dec n#rows)])))

(defn number-of-rows [header-orientations headers]
  (apply max (map height headers header-orientations)))

(defn header-rows [header-orientations headers]
  (let [n#rows (number-of-rows header-orientations headers)
        n#columns (count headers)
        matrix (vec (repeat n#columns (vec (repeat n#rows ""))))]
    (m/transpose (reduce with-header matrix
                         (mapv vector
                               (range)
                               (repeat n#rows)
                               header-orientations
                               headers)))))

(defn header-color-rows [header-colors header-orientations headers]
  (repeat (number-of-rows header-orientations headers)
          header-colors))
