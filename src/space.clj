(ns space
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]))

(defn shape-size
  [shape]
  [(count shape) (count (first shape))])

(defn submatrix-str
  [matrix [x y] [x' y']]
  (take (- x' x)
        (map #(subs % y y')
              (drop x matrix))))

(defn iter-shapes
  [radar n-rows n-columns]
  (let [[x y] (shape-size radar)]
    (into {}
          (for [i (range (inc (- x n-rows)))
                j (range (inc (- y n-columns)))]
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

(defn find-invader
  [radar-signal invader fuzziness]
  (let [[n-rows n-cols] (shape-size invader)]
    (for [[coords sub] (iter-shapes radar-signal n-rows n-cols)
          :when (>= (matching-ratio invader sub) fuzziness)]
      {:coords coords, :ratio (matching-ratio sub invader)})))

(defn detect-invaders
  [radar-signal invaders fuzziness]
  (into {}
        (for [[name invader] invaders]
          {name (find-invader radar-signal invader fuzziness)})))

(defn format-result
  [result]
  (doseq [[inv-name matches] result
          {:keys [coords ratio]} matches]
    (printf "\nFound match for %s with probability %.3f%% at coordinates %s\n"
            inv-name
            (* 100 (double ratio))
            coords)))

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

(comment
  (def r (find-invaders "resources/radar.txt"
                        ["resources/inv1.txt"]
                        0.9))
  (format-result r)

  (println
   (clojure.string/join
    "\n"
    (submatrix-str (parse-file "resources/radar.txt")
                   [13 60]
                   [21 71])))
  )
