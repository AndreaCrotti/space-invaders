#!/usr/bin/env bb

(ns space-script
  (:require [space]
            [babashka.cli :as cli]
            [babashka.fs :as fs]))

(defn file-exists?
  [path]
  (fs/exists? path))

(def cli-spec
  {:spec
   {:invaders
    {:desc     "Invaders definitions"
     :alias    :n
     :validate #(every? true? (map file-exists? %))
     :coerce   #{}
     :require  true}

    :radar
    {:desc     "Radar input"
     :alias    :r
     :validate file-exists?
     :require  true}

    :fuzzyness
    {:desc     "Fuzziness level, 0 is the lowest and 1 is the highest"
     :alias    :f
     :coerce   :double
     :default  0.8
     :validate #(and (> % 0) (<= % 1))}}})

(defn show-help
  [spec]
  (cli/format-opts (merge spec {:order (vec (keys (:spec spec)))})))

(defn -main
  [& _args]
  (let [{:keys [opts]}
        (try (cli/parse-args *command-line-args* cli-spec)
             (catch Exception e
               (println (ex-message e))
               (println (show-help cli-spec))
               (System/exit 1)))]
    (if (or (:help opts) (:h opts))
      (println (show-help cli-spec))
      (println "thinking about it very hard"))))

(-main)
