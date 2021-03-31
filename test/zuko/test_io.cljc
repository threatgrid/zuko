(ns ^{:doc "Testing the io operations. These are unnecessary in Clojure
            but have been included for ClojureScript."
      :author "Paula Gearon"}
    zuko.test-io
    (:require [zuko.io :as io]
              #?(:clj  [clojure.test :as t :refer [deftest is]]
                 :cljs [clojure.test :as t :refer-macros [deftest is]])))

(def fname "io-test.txt")

(deftest test-in-out
  (let [data "The quick brown fox jumps over the lazy dog."]
    (io/spit fname data)
    (is (= data (io/slurp fname)))
    (io/rm fname)))

(deftest test-exists-rm
  (let [data "test data"]
    (is (not (io/exists fname)))
    (is (not (io/rm fname)))
    (io/spit fname data)
    (is (io/exists fname))
    (is (io/rm fname))))

#?(:cljs (t/run-tests))
