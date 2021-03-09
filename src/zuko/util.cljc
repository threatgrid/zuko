(ns zuko.util
    "The ubiquitous utility namespace that every project seems to have"
    (:require [schema.core :as s :refer [=>]]
              [zuko.sandbox :as sandbox])
    #?(:clj (:import [clojure.lang Var])))

;; NOTE: this code avoids cljs.js due to inconsistency in how it gets loaded in standard configurations

#?(:cljs (def known-namespaces {'cljs.core sandbox/allowed-map
                                'clojure.core sandbox/allowed-map
                                'clojure.string sandbox/allowed-map-str
                                "cljs.core" sandbox/allowed-map
                                "clojure.core" sandbox/allowed-map
                                "clojure.string" sandbox/allowed-map-str}))

#?(:clj
   (s/defn get-fn-reference :- (s/maybe Var)
     "Looks up a namespace:name function represented in a keyword,
      and if it exists, return it. Otherwise nil"
     [kw :- (s/cond-pre s/Keyword s/Symbol)]
     (let [kns (namespace kw)
           snm (symbol (name kw))]
       (some-> kns
               symbol
               find-ns
               (ns-resolve snm))))

   :cljs
   (s/defn get-fn-reference :- (s/maybe Var)
     "Looks up a namespace:name function represented in a keyword,
      and if it exists, return it. Otherwise nil"
     [kw :- (s/cond-pre s/Keyword s/Symbol)]
     (let [kns (namespace kw)
           kw-ns (known-namespaces kns)
           snm (symbol (name kw))]
       (when kw-ns (kw-ns snm)))))

#?(:clj
   (def c-eval clojure.core/eval)

   :cljs
   (defn c-eval
     "Equivalent to clojure.core/eval. Returns nil on error."
     [expr & {:as opts}]
     (throw (ex-info "eval not supported in web environment" {:error "No eval support"}))))

#?(:clj
   (defn fn-for
     "Converts a symbol or string representing an operation into a callable function"
     [op]
     (let [ops (if (symbol? op) op (symbol op))]
       (or (sandbox/macro-fns ops)
           (ns-resolve (the-ns 'clojure.core) ops)
           (throw (ex-info (str "Unable to resolve symbol '" op " in "
                                (or (namespace ops) 'clojure.core))
                           {:op op :namespace (or (namespace ops) "clojure.core")})))))

   :cljs
   (defn fn-for
     "Converts a symbol or string representing an operation into a callable function"
     [op]
     (letfn [(resolve-symbol [ns-symbol s]
               (get (get known-namespaces ns-symbol) (symbol (name s))))]
       (let [op-symbol (if (string? op) (symbol op) op)]
         (or
          (if-let [ons-str (namespace op-symbol)]
            (let [ons-symbol (symbol ons-str)]
              (if-let [ns->functions (known-namespaces ons-symbol)]
                (get ns->functions (symbol (name op-symbol)))
                (throw (ex-info (str "Unable to resolve symbol '" op-symbol " in " ons-str)
                         {:op op-symbol :namespace ons-str}))))
            (or (resolve-symbol 'clojure.core op-symbol)
                (resolve-symbol 'cljs.core op-symbol)))
          (throw (ex-info (str "Unable to resolve symbol '" op-symbol " in "
                               (or (namespace op-symbol) 'cljs.core))
                          {:op op-symbol
                           :namespace (or (namespace op-symbol) "cljs.core")})))))))


(s/defn mapmap :- {s/Any s/Any}
  "Creates a map from functions applied to a seq.
   (mapmap (partial * 2) [1 2 3 4 5])
     => {1 2, 2 4, 3 6, 4 8, 5 10}
   (mapmap #(keyword (str \"k\" (dec %))) (partial * 3) [1 2 3])
     => {:k0 3, :k1 6, :k2 9}"
  ([valfn :- (=> s/Any s/Any)
    s :- [s/Any]] (mapmap identity valfn s))
  ([keyfn :- (=> s/Any s/Any)
    valfn :- (=> s/Any s/Any)
    s :- [s/Any]]
    (into {} (map (juxt keyfn valfn) s))))

(s/defn divide' :- [(s/one [s/Any] "true values")
                    (s/one [s/Any] "false values")]
  "Takes a predicate and a sequence and returns 2 sequences.
   The first is where the predicate returns true, and the second
   is where the predicate returns false. Note that a nil value
   will not be returned in either sequence, regardless of the
   value returned by the predicate."
  [p
   s :- [s/Any]]
  (let [decision-map (group-by p s)]
    [(get decision-map true) (get decision-map false)]))

(defn fixpoint
  "Applies the function f to the value a. The function is then,
   and applied to the result, over and over, until the result does not change.
   Returns the final result.
   Note: If the function has no fixpoint, then runs forever."
  [f a]
  (let [s (iterate f a)]
    (some identity (map #(#{%1} %2) s (rest s)))))
