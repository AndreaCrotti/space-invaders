(ns space
  (:require [cljs.core :as c]))

(defn hello-world []
  (println "Hello, World!"))

(c/set! js/window.myApp
        (fn [] (hello-world)))
