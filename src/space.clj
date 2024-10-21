(ns space
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [helpers :refer [parse-file ratio->percent]]))

(defn shape-size
  [shape]
  [(count shape) (count (first shape))])

(defn padding-subs
  [s from to]
  (->>(range from to)
       (map #(nth s % " "))
       (string/join "")))

(defn padding-rows
  [matrix from to length]
  (into []
        (for [l (range from to)]
          (nth matrix l (string/join (repeat length " "))))))

(defn submatrix-str
  [matrix [x y] [x' y']]
  (map #(padding-subs % y y')
       (padding-rows matrix x x' (- y' y))))

(defn iter-shapes
  [radar n-rows n-columns]
  (let [[x y] (shape-size radar)]
    (into {}
          (for [i (range (- (dec n-rows))
                         (- (+ x n-rows) 2))
                j (range (- (dec n-columns))
                         (- (+ y n-columns) 2))]
            [[i j] (vec (submatrix-str radar
                                       [i j]
                                       [(+ i n-rows) (+ j n-columns)]))]))))

(defn count-matching-chars
  [s1 s2]
  (get (frequencies (map (fn [[f s]]
                           (= f s))
                         (map vector s1 s2)))
       true
       0))

(defn matching-ratio
  [shape-1 shape-2]
  (assert (= (count shape-1) (count shape-2)), "Shapes must have the same size")
  (let [[x y]   (shape-size shape-1)
        n-chars (* x y)]
    (/ (reduce +
               (for [[s1 s2] (zipmap shape-1 shape-2)]
                 (count-matching-chars s1 s2)))
       n-chars)))

(defn end-coords
  [[x y] invader]
  (let [[n-rows n-cols] (shape-size invader)]
    [(+ x n-rows) (+ y n-cols)]))

(defn find-invader
  [radar invader fuzziness]
  (let [[n-rows n-cols] (shape-size invader)]
    (for [[coords sub] (iter-shapes radar n-rows n-cols)
          :when        (>= (matching-ratio invader sub) fuzziness)]
      {:start coords
       :end   (end-coords coords invader)
       :ratio (matching-ratio sub invader)})))

(defn detect-invaders
  [radar invaders fuzziness]
  (into {}
        (for [[name invader] invaders]
          {name (find-invader radar invader fuzziness)})))

(defn sort-matches
  [ms]
  (->> ms
       (sort-by :ratio)
       reverse))

(defn format-match
  [match]
  (->> match
       ;; trimming white space on the right is just to avoid trailing whitespaces in the test data
       (map (comp string/trimr #(string/replace % "-" " ")))
       (string/join "\n")))

(defn find-invaders
  [radar-file invader-files fuzziness]
  (let [radar    (parse-file radar-file)
        invaders (into {}
                       (for [if invader-files]
                         [(.getName (io/file if)) (parse-file if)]))]
    (detect-invaders radar invaders fuzziness)))

(defn format-result
  [radar result]
  (for [[inv-name matches]        result
        {:keys [start end ratio]} (sort-matches matches)
        :let                      [match (submatrix-str radar start end)]]

    (format "\nFound match for %s with probability %.3f%% from %s to %s\n%s\n"
            inv-name
            (ratio->percent ratio)
            start
            end
            (format-match match))))
