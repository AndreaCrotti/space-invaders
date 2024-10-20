(ns space-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [space]))

(def simple-invader
  ["-o-"
   "oo-"
   "ooo"])

(def radar-signal
  ["----"
   "--o-"
   "-oo-"
   "oooo"])

(deftest space-test
  (testing "Can find coordinates"
    (is (= [{:start [1 1], :end [4 4], :ratio 1}]
           (space/find-invader radar-signal simple-invader 1)))))

(deftest matching-test
  (is (= (/ 1 3)
         ;; two chars out of six match
         (space/matching-ratio ["001" "000"] ["110" "001"]))))

(deftest submatrix-test
  (is (= ["ab" "de"]
         (space/submatrix-str ["abc" "def" "xyz"] [0 0] [2 2]))))

(deftest iter-shapes-test
  (testing "Can also handle edges correctly"
    (is (= {[0 0] ["oo" "--"],
            [1 0] ["--" "  "],
            [-1 0] ["  " "oo"],
            [1 1] ["- " "  "],
            [-1 -1] ["  " " o"],
            [1 -1] [" -" "  "],
            [-1 1] ["  " "o "],
            [0 -1] [" o" " -"],
            [0 1] ["o " "- "]}
           (space/iter-shapes ["oo" "--"] 2 2)))))

(deftest find-invaders-test
  (is (= {"inv1.txt" [{:start [13 60], :end [21 71] , :ratio (/ 10 11)}]}
         (space/find-invaders "resources/radar.txt" ["resources/inv1.txt"] 0.9))))
