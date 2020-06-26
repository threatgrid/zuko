(ns zuko.test-entity
  (:require [zuko.entity.writer :refer [string->triples entities->triples entity-update->triples ident-map->triples]]
            [zuko.entity.reader :refer [graph->entities ref->entity]]
            [zuko.helper-stub :as test-helper]
            [asami.multi-graph]
            [asami.core :refer [empty-graph assert-data retract-data q]]
            #?(:clj  [schema.test :as st :refer [deftest]]
               :cljs [schema.test :as st :refer-macros [deftest]])
            #?(:clj  [clojure.test :as t :refer [is]]
               :cljs [clojure.test :as t :refer-macros [is]]))
  #?(:clj (:import [java.time ZonedDateTime])
     :cljs (:import [goog.date DateTime])))

(defn parseDateTime [s]
  #?(:clj (ZonedDateTime/parse s)
     :cljs (DateTime.fromRfc822String s)))

(t/use-fixtures :once st/validate-schemas)

(deftest test-encode-from-string
  (let [m1 (string->triples (test-helper/new-graph)
                            "[{\"prop\": \"val\"}]")
        m2 (string->triples (test-helper/new-graph)
                            "[{\"prop\": \"val\", \"p2\": 2}]")
        m3 (string->triples (test-helper/new-graph)
                            (str "[{\"prop\": \"val\","
                                 "  \"p2\": 22,"
                                 "  \"p3\": [42, 54]}]"))
        m4 (string->triples (test-helper/new-graph)
                            (str "[{\"prop\": \"val\"},"
                                 " {\"prop\": \"val2\"}]"))
        m5 (string->triples (test-helper/new-graph)
                            (str "[{\"prop\": \"val\","
                                 "  \"arr\": ["
                                 "    {\"a\": 1},"
                                 "    {\"a\": 2},"
                                 "    [\"nested\"]"
                                 "]}]"))]
    (is (= [[:test/n1 :db/ident :test/n1]
            [:test/n1 :tg/entity true]
            [:test/n1 :prop "val"]] m1))
    (is (= [[:test/n1 :db/ident :test/n1]
            [:test/n1 :tg/entity true]
            [:test/n1 :prop "val"]
            [:test/n1 :p2 2]] m2))
    (is (= [[:test/n1 :db/ident :test/n1] 
            [:test/n1 :tg/entity true]
            [:test/n1 :prop "val"]
            [:test/n1 :p2 22]
            [:test/n1 :p3 :test/n2]
            [:test/n2 :tg/first 42]
            [:test/n2 :tg/rest :test/n3]
            [:test/n3 :tg/first 54]
            [:test/n2 :tg/contains 42]
            [:test/n2 :tg/contains 54]] m3))
    (is (= [[:test/n1 :db/ident :test/n1]
            [:test/n1 :tg/entity true]
            [:test/n1 :prop "val"]
            [:test/n2 :db/ident :test/n2]
            [:test/n2 :tg/entity true]
            [:test/n2 :prop "val2"]] m4))
    (is (= [[:test/n1 :db/ident :test/n1]
            [:test/n1 :tg/entity true]
            [:test/n1 :prop "val"]
            [:test/n1 :arr :test/n2]
            [:test/n2 :tg/first :test/n3]
            [:test/n2 :tg/rest :test/n4]
            [:test/n3 :a 1]
            [:test/n4 :tg/first :test/n5]
            [:test/n4 :tg/rest :test/n6]
            [:test/n5 :a 2]
            [:test/n6 :tg/first :test/n7]
            [:test/n7 :tg/first "nested"]
            [:test/n7 :tg/contains "nested"]
            [:test/n2 :tg/contains :test/n3]
            [:test/n2 :tg/contains :test/n5]
            [:test/n2 :tg/contains :test/n7]] m5))))

(deftest test-encode
  (let [m1 (entities->triples (test-helper/new-graph)
                          [{:prop "val"}])
        m2 (entities->triples (test-helper/new-graph)
                          [{:prop "val", :p2 2}])
        m3 (entities->triples (test-helper/new-graph)
                          [{:prop "val", :p2 22, :p3 [42 54]}])
        m4 (entities->triples (test-helper/new-graph)
                          [{:prop "val"} {:prop "val2"}])
        m5 (entities->triples (test-helper/new-graph)
                          [{:prop "val"
                            :arr [{:a 1} {:a 2} ["nested"]]}])]
    (is (= [[:test/n1 :db/ident :test/n1]
            [:test/n1 :tg/entity true]
            [:test/n1 :prop "val"]] m1))
    (is (= [[:test/n1 :db/ident :test/n1]
            [:test/n1 :tg/entity true]
            [:test/n1 :prop "val"]
            [:test/n1 :p2 2]] m2))
    (is (= [[:test/n1 :db/ident :test/n1] 
            [:test/n1 :tg/entity true]
            [:test/n1 :prop "val"]
            [:test/n1 :p2 22]
            [:test/n1 :p3 :test/n2]
            [:test/n2 :tg/first 42]
            [:test/n2 :tg/rest :test/n3]
            [:test/n3 :tg/first 54]
            [:test/n2 :tg/contains 42]
            [:test/n2 :tg/contains 54]] m3))
    (is (= [[:test/n1 :db/ident :test/n1] 
            [:test/n1 :tg/entity true]
            [:test/n1 :prop "val"]
            [:test/n2 :db/ident :test/n2] 
            [:test/n2 :tg/entity true]
            [:test/n2 :prop "val2"]] m4))
    (is (= [[:test/n1 :db/ident :test/n1] 
            [:test/n1 :tg/entity true]
            [:test/n1 :prop "val"]
            [:test/n1 :arr :test/n2]
            [:test/n2 :tg/first :test/n3]
            [:test/n2 :tg/rest :test/n4]
            [:test/n3 :a 1]
            [:test/n4 :tg/first :test/n5]
            [:test/n4 :tg/rest :test/n6]
            [:test/n5 :a 2]
            [:test/n6 :tg/first :test/n7]
            [:test/n7 :tg/first "nested"]
            [:test/n7 :tg/contains "nested"]
            [:test/n2 :tg/contains :test/n3]
            [:test/n2 :tg/contains :test/n5]
            [:test/n2 :tg/contains :test/n7]] m5))))

(defn round-trip
  [data]
  (let [m (entities->triples empty-graph (seq data))
        new-db (assert-data empty-graph m)]
    (set (graph->entities new-db))))

(deftest test-round-trip
  (let [d1 #{{:prop "val"}}
        dr1 (round-trip d1)

        d2 #{{:prop "val", :p2 2}}
        dr2 (round-trip d2)

        d3 #{{:prop "val", :p2 22, :p3 [42 54]}}
        dr3 (round-trip d3)

        d4 #{{:prop "val"} {:prop "val2"}}
        dr4 (round-trip d4)

        d5 #{{:prop "val" :arr [{:a 1} {:a 2} ["nested"]]}}
        dr5 (round-trip d5)

        d6 #{{:prop "val" :arr [{:a 1} {:a 2} ["nested"]] :nested {}}}
        dr6 (round-trip d6)

        d7 #{{:prop "val" :arr (map identity [{:a 1} {:a 2} ["nested"]]) :nested {}}}
        dr7 (round-trip d7)

        d8 #{{:prop "val" :arr #{{:a 1} {:a 2} ["nested"]}}}
        dr8 (set (map #(update % :arr set) (round-trip d8)))]
    (is (= d1 dr1))
    (is (= d2 dr2))
    (is (= d3 dr3))
    (is (= d4 dr4))
    (is (= d5 dr5))
    (is (= d6 dr6))
    (is (= d7 dr7))
    (is (= d8 dr8))))

(defn generate-diff
  [o1 o2]
  (let [triples (entities->triples empty-graph [o1])
        props (filter (fn [[k v]] (or (number? v) (string? v))) o1)
        gr (assert-data empty-graph triples)
        id (ffirst (q (concat '[:find ?id :where] (map (fn [[k v]] ['?id k v]) props)) gr))
        [additions retractions] (entity-update->triples gr id o2)]
    [id additions retractions]))

(deftest test-updates
  (let [
        [id1 add1 ret1] (generate-diff {:a 1} {:a 2})
        [id2 add2 ret2] (generate-diff {:a 1 :b "foo"} {:a 2 :b "foo"})
        [id3 add3 ret3] (generate-diff {:a 1 :b "foo"} {:a 1 :b "bar"})
        [id4 add4 ret4] (generate-diff {:a 1 :b "foo" :c [10 11 12] :d {:x "a" :y "b"} :e [{:m 1} {:m 2}]}
                                       {:b "bar", :c [10 10 12], :e [{:m 1} {:m 2}], :bx "xxx"})]
    (is (= add1 [[id1 :a 2]]))
    (is (= ret1 [[id1 :a 1]]))
    (is (= add2 [[id2 :a 2]]))
    (is (= ret2 [[id2 :a 1]]))
    (is (= add3 [[id3 :b "bar"]]))
    (is (= ret3 [[id3 :b "foo"]]))
    (let [adds (filter #(#{:b :c :bx} (second %)) add4)
          dels (filter #(#{:a :b :c :d} (second %)) ret4)
          [[sid1 _ a1] [sid2 _ a2] :as ds] (filter (fn [[_ p _]] (#{:x :y} p)) ret4)]
      (is (= 3 (count adds)))
      (is (= 4 (count dels)))
      (is (= sid1 sid2))
      (is (= #{"a" "b"} #{a1 a2})))))

(defn get-node-ref
  [graph id]
  (ffirst (q [:find '?n :where ['?n :id id]] graph)))


(deftest test-ref->entity
  (let [data {:id "1234" :prop "value" :attribute 2}
        m (entities->triples empty-graph [data])
        graph' (assert-data empty-graph m)
        ref (get-node-ref graph' "1234")
        graph (assert-data graph' [[ref "Connected_To" ref]])
        obj1 (ref->entity graph ref)
        obj2 (ref->entity graph ref #{"Connected_To"})]
    (is (= data obj1))
    (is (= data obj2))))

#?(:cljs (def importer asami.multi-graph.MultiGraph))

(deftest test-multi-update
  (let [graph
        #asami.multi_graph.MultiGraph{:spo #:mem{:node-27367
                                                 {:db/ident #:mem{:node-27367 {:count 1}},
                                                  :naga/entity {true {:count 1}},
                                                  :value {"01468b1d3e089985a4ed255b6594d24863cfd94a647329c631e4f4e52759f8a9" {:count 1}},
                                                  :type {"sha256" {:count 1}},
                                                  :id {"4f390192" {:count 1}}}},
                                      :pos {:db/ident
                                            #:mem{:node-27367 #:mem{:node-27367 {:count 1}}},
                                            :naga/entity {true #:mem{:node-27367 {:count 1}}},
                                            :value {"01468b1d3e089985a4ed255b6594d24863cfd94a647329c631e4f4e52759f8a9" #:mem{:node-27367 {:count 1}}},
                                            :type {"sha256" #:mem{:node-27367 {:count 1}}},
                                            :id {"4f390192" #:mem{:node-27367 {:count 1}}}},
                                      :osp {:mem/node-27367 #:mem{:node-27367 #:db{:ident {:count 1}}},
                                            true #:mem{:node-27367 #:naga{:entity {:count 1}}},
                                            "01468b1d3e089985a4ed255b6594d24863cfd94a647329c631e4f4e52759f8a9"
                                            #:mem{:node-27367 {:value {:count 1}}},
                                            "sha256" #:mem{:node-27367 {:type {:count 1}}},
                                            "4f390192" #:mem{:node-27367 {:id {:count 1}}}}}
        id "verdict:AMP File Reputation:4f390192"
        m {:type "verdict",
           :disposition 2,
           :observable {:value "01468b1d3e089985a4ed255b6594d24863cfd94a647329c631e4f4e52759f8a9",
                        :type "sha256"},
           :disposition_name "Malicious",
           :valid_time {:start_time (parseDateTime "2017-12-05T12:45:32.192Z"),
                        :end_time (parseDateTime "2525-01-01T00:00Z")},
           :module-name "AMP File Reputation",
           :id "verdict:AMP File Reputation:4f390192"}
        new-graph (if-let [n (get-node-ref graph id)]
                    (let [[assertions retractions] (entity-update->triples graph n m)
                          assert-keys (set (map (fn [[a b c]] [a b]) assertions))
                          retract-existing (filter (fn [[a b c]] (assert-keys [a b])) retractions)]
                      (-> graph
                          (retract-data retract-existing)
                          (assert-data assertions)))
                    (let [[assertions] (ident-map->triples graph (assoc m :id id))]
                      (assert-data graph assertions)))]
    (is (= 4 (count (:spo new-graph))))))

#?(:cljs (t/run-tests))
