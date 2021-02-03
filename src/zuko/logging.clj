(ns ^{:doc "Simple logging facilities.
If the log level is set to 0 at compile time, then there will be no impact on genereated code.
Otherwise, the current binding of *level* will determine which logs will be emitted,
and the current binding of *output* will determine where the log will be sent."
      :author "Paula Gearon"}
    zuko.logging
    (:require [clojure.string :as s])
    (:import [clojure.lang Atom]
             [java.io Writer]))

(def logging-enabled "logging.enabled")

(def clojurescript? (if (->> (.getStackTrace (ex-info "" {}))
                             (map #(.getClassName %))
                             (some #(re-matches #".*cljs.*" %)))
                      "true"
                      "false"))

;; This value is used to control compilation of the logging macros.
;; If it is set during compilation, then macros will generate code.
;; In Clojure this will default to off, unless enabled with -Dlogging.enabled=true
;; but can be modified programatically during compilation.
(defonce ^:dynamic *enabled* (Boolean/parseBoolean (System/getProperty logging-enabled clojurescript?)))

;; level 0 has no logging. The higher the level, the more logging output.
(def ^:dynamic *level* 0)

;; 
(def ^:dynamic *output* nil)

(def log-level
  {:trace 6
   :debug 5
   :info 4
   :warn 3
   :error 2
   :fatal 1})

(def log-label (into {} (map (fn [[k v]] [v k]) log-level)))

(defn set-enabled!
   "Sets whether or not this library is enabled"
   [enabled]
   (alter-var-root #'*enabled* (constantly enabled)))

(defn set-logging-level!
  "Set the level of logging. This avoids needing to bind the logging level dynamically during compilation.
  The level may be numerical of by keyword."
  [level]
  (let [n (if (number? level) level (log-level level 0))]
    (alter-var-root #'*level* (constantly n))))

(defn set-output!
  "Sets the output. This avoids using a dynamic binding, if desired.
  valid outputs are:
  nil or \"\" - write to stdout
  (atom []) - accumulate log in a vector
  java.io.Writer - send to a writer (Clojure only)."
  [output]
  (alter-var-root #'*output* (constantly output)))

(defprotocol LogOutput
  (emit [dest text] "sends a line of text to the appropriate output"))

(extend-protocol LogOutput
  String
  (emit [dest text]
    (if (empty? dest)
      (println text)
      (spit dest (str text "\n") :append true)))

  Atom
  (emit [dest text]
    (swap! dest conj text))

  Writer
  (emit [dest text]
    (.append dest text)
    (.append dest \newline)
    (.flush dest))

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

(defmacro trace
  [& args]
  (when *enabled*
    (let [cns (str *ns*)]
      `(when (<= 6 *level*) (log* *output* :trace ~cns ~@args)))))

(defmacro debug
  [& args]
  (when *enabled*
    (let [cns (str *ns*)]
      `(when (<= 5 *level*) (log* *output* :debug ~cns ~@args)))))

(defmacro info
  [& args]
  (when *enabled*
    (let [cns (str *ns*)]
      `(when (<= 4 *level*) (log* *output* :info ~cns ~@args)))))

(defmacro warn
  [& args]
  (when *enabled*
    (let [cns (str *ns*)]
      `(when (<= 3 *level*) (log* *output* :warn ~cns ~@args)))))

(defmacro error
  [& args]
  (when *enabled*
    (let [cns (str *ns*)]
      `(when (<= 2 *level*) (log* *output* :error ~cns ~@args)))))

(defmacro fatal
  [& args]
  (when *enabled*
    (let [cns (str *ns*)]
      `(when (<= 1 *level*) (log* *output* :fatal ~cns ~@args)))))
