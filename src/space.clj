(ns space
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]))

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
  (let [[x y] (shape-size shape-1)
        n-chars (* x y)]
    (/ (reduce +
               ;; TODO: could be improved slightly here
               (for [i (range (count shape-1))]
                 (count-matching-chars (get shape-1 i)
                                       (get shape-2 i))))
       n-chars)))

(defn end-coords
  [[x y] invader]
  (let [[n-rows n-cols] (shape-size invader)]
    [(+ x n-rows) (+ y n-cols)]))

(defn find-invader
  [radar-signal invader fuzziness]
  (let [[n-rows n-cols] (shape-size invader)]
    (for [[coords sub] (iter-shapes radar-signal n-rows n-cols)
          :when (>= (matching-ratio invader sub) fuzziness)]
      {:coords [coords (end-coords coords invader)]
       :ratio (matching-ratio sub invader)})))

(defn detect-invaders
  [radar-signal invaders fuzziness]
  (into {}
        (for [[name invader] invaders]
          {name (find-invader radar-signal invader fuzziness)})))

(defn- ratio->percent
  [ratio]
  (* 100 (double ratio)))

(defn sort-matches
  [ms]
  (->> ms
       (sort-by :ratio)
       reverse))

(defn format-match
  [match]
  (-> (string/join "\n" match)
      (string/replace "-" " ")))

(defn format-result
  [radar result]
  (doseq [[inv-name matches] result
          {:keys [coords ratio]} (sort-matches matches)
          :let [[start end] coords
                match (submatrix-str radar start end)]]

    (printf "\nFound match for %s with probability %.3f%% from %s to %s\n%s\n"
            inv-name
            (ratio->percent ratio)
            start
            end
            (format-match match))))

(defn parse-file
  [f]
  (-> f
      slurp
      string/split-lines))

(defn find-invaders
  [radar-file invader-files fuzziness]
  (let [radar (parse-file radar-file)
        invaders (into {}
                       (for [if invader-files]
                         [(.getName (io/file if)) (parse-file if)]))]
    (detect-invaders radar invaders fuzziness)))
