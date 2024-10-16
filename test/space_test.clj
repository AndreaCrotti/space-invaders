(ns space-test
  (:require [space]
            [clojure.test :refer [deftest testing is]]))

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
    (is (= [1 1]
           (space/invaders-coordinates radar-signal [simple-invader])))))
