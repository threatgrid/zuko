(ns zuko.helper-stub
  (:require [zuko.entity.graph-api :as api]
            #?(:clj  [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])))

(declare ->TestStore)

(s/defrecord TestStore [data n]
  api/SimpleGraphAPI
  (new-node [store]
    (let [v (swap! n inc)]
      (keyword "test" (str "n" v))))
  (node-type? [store p n] (and (keyword? n)
                               (= "test" (namespace n))
                               (= \n (first (name n)))))
  (data-property [store data] :tg/first)
  (container-property [store data] :tg/contains)
  (resolve-pattern [store pattern] data))

(defn new-store [] (->TestStore [0] (atom 0)))

(def empty-store (new-store))
