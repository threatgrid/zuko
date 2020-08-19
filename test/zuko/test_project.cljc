(ns zuko.test-project
  (:require [clojure.test :as t :refer [deftest is run-tests]]
            [clojure.string]
            [zuko.projection :refer [project project-single project-collection project-tuple project-results]])
  #?(:clj (:import [clojure.lang ExceptionInfo])))


(deftest test-project-tuple
  (let [tuple '[?a ?b ?c]
        columns '[?q ?t ?c ?b ?z ?a]
        data [[1 2 3 4 5 6] [7 8 9 10 11 12]]]
    (is (= [6 4 3] (project-tuple tuple columns data)))
    (is (= nil (project-tuple tuple columns nil)))
    (is (= nil (project-tuple tuple columns '())))
    (is (thrown-with-msg? ExceptionInfo #"Projection variables not found in the selected data: \[\?a\]"
                          (project-tuple tuple '[?q ?t ?c ?b ?z ?d] data)))

    (let [mdata (with-meta data {:cols columns})]
      (is (= [6 4 3] (project {} [tuple] mdata)))
      (is (= [3] (project {} '[[?c]] mdata)))
      (is (= nil (project {} '[[?c]] (with-meta '() {:cols columns})))))))

(deftest test-project-single
  (let [columns '[?q ?t ?c ?b ?z ?a]
        data [[1 2 3 4 5 6] [7 8 9 10 11 12]]]
    (is (= 3 (project-single '?c columns data)))
    (is (thrown-with-msg? ExceptionInfo #"Projection variable was not in the selected data: \?c"
                          (project-single '?c '[?q ?t ?a ?b ?z ?d] data)))

    (let [mdata (with-meta data {:cols columns})]
      (is (= 3 (project {} '[?c .] mdata))))))

(deftest test-project-collection
  (let [columns '[?q ?t ?c ?b ?z ?a]
        data [[1 2 3 4 5 6] [7 8 9 10 11 12]]]
    (is (= [3 9] (project-collection '?c columns data)))
    (is (thrown-with-msg? ExceptionInfo #"Projection variable was not in the selected data: \?c"
                          (project-single '?c '[?q ?t ?a ?b ?z ?d] data)))

    (let [mdata (with-meta data {:cols columns})]
      (is (= [3 9] (project {} '[[?c ...]] mdata))))))

(deftest test-project-results
  (let [selection '[?a ?b ?c]
        columns '[?q ?t ?c ?b ?z ?a]
        data [[1 2 3 4 5 6] [7 8 9 10 11 12]]]
    (is (= [[6 4 3] [12 10 9]] (project-results {} selection columns data)))

    (let [mdata (with-meta data {:cols columns})]
      (is (= [[6 4 3] [12 10 9]] (project {} selection mdata))))))


#?(:cljs (run-tests))
