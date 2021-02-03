(ns ^{:doc "Simple logging facilities.
If the log level is set to 0 at compile time, then there will be no impact on genereated code.
Otherwise, the current binding of *level* will determine which logs will be emitted,
and the current binding of *output* will determine where the log will be sent."
      :author "Paula Gearon"}
    zuko.logging
    (:require [clojure.string :as s]))

(def nodefs (try (js/require "fs") (catch :default _ nil)))

;; level 0 has no logging. The higher the level, the more logging output.
(def ^:dynamic *level* 0)

(def ^:dynamic *output* nil)

(def log-level
  {:trace 6
   :debug 5
   :info 4
   :warn 3
   :error 2
   :fatal 1})

(def log-label (into {} (map (fn [[k v]] [v k]) log-level)))

(defn set-logging-level!
  "Set the level of logging. This avoids needing to bind the logging level dynamically during compilation.
  The level may be numerical of by keyword."
  [level]
  (let [n (if (number? level) level (log-level level 0))]
    (set! *level* n)))

(defn set-output!
  "Sets the output. This avoids using a dynamic binding, if desired.
  valid outputs are:
  nil or \"\" - write to stdout
  (atom []) - accumulate log in a vector
  java.io.Writer - send to a writer (Clojure only)."
  [output]
  (set! *output* output))

(defprotocol LogOutput
  (emit [dest text] "sends a line of text to the appropriate output"))

(extend-protocol LogOutput
  string
  (emit [dest text]
    (if (empty? dest)
      (println text)
      (when nodefs
        (.appendFileSync nodefs dest (str text "\n")))))

  Atom
  (emit [dest text]
    (swap! dest conj text))

  nil
  (emit [dest text] (println text)))

(defn log*
  [log-output level cns & data]
  (when-let [l (if (keyword? level)
                 level
                 (log-label level))]
    (when (<= (log-level l) (log-level *level* *level*))
      (let [text (str (if (seq cns) cns "<unknown>") " "
                      (s/upper-case (name l)) ": " (apply str data))]
        (emit *output* text)))))
