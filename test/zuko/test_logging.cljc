(ns ^{:doc "Logging test"
      :author "Paula Gearon"}
    zuko.test-xlogging
    (:require
     [zuko.logging :as log]
     [zuko.helper-stub]
     #?(:clj [clojure.test :refer [deftest is]]
        :cljs [clojure.test :refer-macros [deftest is run-tests]])
     #?(:cljs [cljs.js :as cjs]))
    #?(:clj (:import [java.io StringWriter])))

#?(:clj
   (deftest test-stdout
     (log/set-logging-level! 0)
     (let [w (StringWriter.)]
       (binding [log/*output* w]
         (require '[zuko.helper-stub] :reload-all)
         ;; check that it compiled to not log
         (zuko.helper-stub/test-logger "test")
         (is (empty? (str w)))
         (binding [log/*level* 5]
           (zuko.helper-stub/test-logger "test")
           (is (empty? (str w)))
           ;; recompile with it turned on
           (require '[zuko.helper-stub] :reload-all)
           (zuko.helper-stub/test-logger "test")
           (is (= "INFO: test\n" (str w))))
         ;; compiled on, but the level is back to zero, so no new output
         (zuko.helper-stub/test-logger "test")
         (is (= "INFO: test\n" (str w)))
         ;; increase the level, but not enough for "info" level
         (binding [log/*level* 2]
           (zuko.helper-stub/test-logger "test")
           (is (= "INFO: test\n" (str w))))
         ;; increase the level to create output
         (binding [log/*level* 4]
           (zuko.helper-stub/test-logger "test")
           (is (= "INFO: test\nINFO: test\n" (str w))))
         ;; set the root of the level to see if it still outputs
         (log/set-logging-level! 4)
         (zuko.helper-stub/test-logger "test")
         (is (= "INFO: test\nINFO: test\nINFO: test\n" (str w)))
         (log/set-logging-level! 0)))))


#?(:cljs
   (defn ev
     [data]
     (cjs/eval-str (cjs/empty-state)
                   data
                   ""
                   {:eval cjs/js-eval}
                   identity)))

#?(:clj
   (deftest test-log-data
     (log/set-logging-level! 0)
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
           (require '[zuko.helper-stub] :reload-all)
           (zuko.helper-stub/test-logger "test")
           (is (= ["INFO: test"] @w)))
         ;; compiled on, but the level is back to zero, so no new output
         (zuko.helper-stub/test-logger "test")
         (is (= ["INFO: test"] @w))
         ;; increase the level, but not enough for "info" level
         (binding [log/*level* 2]
           (zuko.helper-stub/test-logger "test")
           (is (= ["INFO: test"] @w)))
         ;; increase the level to create output
         (binding [log/*level* 4]
           (zuko.helper-stub/test-logger "test")
           (is (= ["INFO: test" "INFO: test"] @w)))
         ;; set the root of the level to see if it still outputs
         (log/set-logging-level! 4)
         (zuko.helper-stub/test-logger "test")
         (is (= ["INFO: test" "INFO: test" "INFO: test"] @w))))
     (log/set-logging-level! 0))

   :cljs
   (deftest test-log-data
     (log/set-logging-level! 0)
     (let [w (atom [])]
       (binding [log/*output* w]
         ;; check that it compiled to not log
         (zuko.helper-stub/test-logger "test")
         (is (empty? @w))
         (binding [log/*level* 5]
           (zuko.helper-stub/test-logger "test")
           (is (empty? @w))
           ;; recompile with it turned on
           (let [tl (ev "(fn [] (log/info \"test\"))")]
             (tl "test"))
           (is (= ["INFO: test"] @w)))
         ;; compiled on, but the level is back to zero, so no new output
         (zuko.helper-stub/test-logger "test")
         (is (= ["INFO: test"] @w))
         ;; increase the level, but not enough for "info" level
         (binding [log/*level* 2]
           (zuko.helper-stub/test-logger "test")
           (is (= ["INFO: test"] @w)))
         ;; increase the level to create output
         (binding [log/*level* 4]
           (zuko.helper-stub/test-logger "test")
           (is (= ["INFO: test" "INFO: test"] @w)))
         ;; set the root of the level to see if it still outputs
         (log/set-logging-level! 4)
         (zuko.helper-stub/test-logger "test")
         (is (= ["INFO: test" "INFO: test" "INFO: test"] @w))))
     (log/set-logging-level! 0)))
