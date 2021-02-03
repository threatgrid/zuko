(ns ^{:doc "Logging test"
      :author "Paula Gearon"}
    zuko.test-xlogging
    (:require
     [zuko.logging :as log]
     [zuko.helper-stub :include-macros true]
     #?(:clj [clojure.test :refer [deftest is]]
        :cljs [clojure.test :as t :refer-macros [deftest is]])
     #?(:cljs [cljs.js :as cjs]))
    #?(:clj (:import [java.io StringWriter])))

(def text "zuko.helper-stub INFO: test")
(def textn (str text "\n"))

#?(:clj
   (deftest test-stdout
     (log/set-logging-level! 0)
     (log/set-enabled! false)
     (let [w (StringWriter.)]
       (binding [log/*output* w]
         (require '[zuko.helper-stub] :reload-all)
         ;; check that it compiled to not log
         (zuko.helper-stub/test-logger "test")
         (is (empty? (str w)))
         (binding [log/*level* 5]
           ;; check that it compiled to not log
           (zuko.helper-stub/test-logger "test")
           (is (empty? (str w)))
           ;; recompile with it turned on
           (log/set-enabled! true)
           (require '[zuko.helper-stub] :reload-all)
           (zuko.helper-stub/test-logger "test")
           (is (= textn (str w))))
         ;; compiled on, but the level is back to zero, so no new output
         (zuko.helper-stub/test-logger "test")
         (is (= textn (str w)))
         ;; increase the level, but not enough for "info" level
         (binding [log/*level* 2]
           (zuko.helper-stub/test-logger "test")
           (is (= textn (str w))))
         ;; increase the level to create output
         (binding [log/*level* 4]
           (zuko.helper-stub/test-logger "test")
           (is (= (str textn textn) (str w))))
         ;; set the root of the level to see if it still outputs
         (log/set-logging-level! 4)
         (zuko.helper-stub/test-logger "test")
         (is (= (str textn textn textn) (str w)))
         (log/set-logging-level! 0)))))


#?(:clj
   (deftest test-log-data
     (log/set-logging-level! 0)
     (log/set-enabled! false)
     (let [w (atom [])]
       (binding [log/*output* w]
         (require '[zuko.helper-stub] :reload-all)
         ;; check that it compiled to not log
         (zuko.helper-stub/test-logger "test")
         (is (empty? @w))
         (binding [log/*level* 5]
           ;; check that it compiled to not log
           (zuko.helper-stub/test-logger "test")
           (is (empty? @w))
           ;; recompile with it turned on
           (log/set-enabled! true)
           (require '[zuko.helper-stub] :reload-all)
           (zuko.helper-stub/test-logger "test")
           (is (= [text] @w)))
         ;; compiled on, but the level is back to zero, so no new output
         (zuko.helper-stub/test-logger "test")
         (is (= [text] @w))
         ;; increase the level, but not enough for "info" level
         (binding [log/*level* 2]
           (zuko.helper-stub/test-logger "test")
           (is (= [text] @w)))
         ;; increase the level to create output
         (binding [log/*level* 4]
           (zuko.helper-stub/test-logger "test")
           (is (= [text text] @w)))
         ;; set the root of the level to see if it still outputs
         (log/set-logging-level! 4)
         (zuko.helper-stub/test-logger "test")
         (is (= [text text text] @w))))
     (log/set-logging-level! 0))

   :cljs
   (deftest test-log-datas
     (log/set-logging-level! 0)
     (let [w (atom [])]
       (binding [log/*output* w]
         ;; check that it compiled to not log
         (zuko.helper-stub/test-logger "test")
         (is (empty? @w))
         (binding [log/*level* 3]
           (zuko.helper-stub/test-logger "test")
           (is (empty? @w)))
         ;; increase the level to create output
         (binding [log/*level* 4]
           (zuko.helper-stub/test-logger "test")
           (is (= [text] @w)))
         (binding [log/*level* 5]
           (zuko.helper-stub/test-logger "test")
           (is (= [text text] @w)))
         ;; set the root of the level to see if it still outputs
         (log/set-logging-level! 4)
         (zuko.helper-stub/test-logger "test")
         (is (= [text text text] @w))))
     (log/set-logging-level! 0)))

#?(:cljs
   (t/run-tests))
