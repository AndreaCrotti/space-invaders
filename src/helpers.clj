(ns helpers
  (:require
   [clojure.string :as string]))

(defn ratio->percent
  [ratio]
  (* 100 (double ratio)))

(defn parse-file
  [f]
  (-> f
      slurp
      string/split-lines))
