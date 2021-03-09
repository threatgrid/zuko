(ns ^{:doc "Describes allowed functions in a sandbox environment"
      :author "Paula Gearon, Mario Aquino"}
    zuko.sandbox
  (:require [clojure.string]))

(def allowed-fns
  #{'* '*' '+ '+' '- '-' '/ '< '<= '= '== '> '>= 'aget 'alength 'and 'any? 'apply 'array-map
    'aset 'aset-boolean 'aset-byte 'aset-char 'aset-double 'aset-float 'aset-int 'aset-long
    'aset-short 'assoc 'assoc! 'assoc-in 'associative? 'bases 'bean 'bigdec 'bigint 'biginteger
    'bit-and 'bit-and-not 'bit-clear 'bit-flip 'bit-not 'bit-or 'bit-set 'bit-shift-left
    'bit-shift-right 'bit-test 'bit-xor 'boolean 'boolean-array 'boolean? 'booleans 'bounded-count
    'butlast 'byte 'byte-array 'bytes 'bytes? 'cat 'char 'char-array 'char-escape-string
    'char-name-string 'char? 'chars 'class 'class? 'clojure-version 'coll? 'comp 'comparator 'compare
    'compare-and-set! 'complement 'completing 'concat 'conj 'conj! 'cons 'constantly
    'construct-proxy 'contains? 'count 'counted? 'create-struct 'cycle 'dec 'dec' 'decimal?
    'dedupe 'default-data-readers 'delay? 'deliver 'denominator 'deref 'derive 'descendants 'disj
    'disj! 'dissoc 'dissoc! 'distinct 'distinct? 'doall 'dorun 'double 'double-array 'double?
    'doubles 'drop 'drop-last 'drop-while 'eduction 'empty 'empty? 'ensure-reduced
    'enumeration-seq 'even? 'every-pred 'every? 'extends? 'false? 'ffirst 'filter 'filterv 'find
    'find-keyword 'first 'flatten 'float 'float-array 'float? 'floats 'fn? 'fnext 'fnil 'force
    'format 'frequencies 'gensym 'get 'get-in 'group-by 'halt-when 'hash 'hash-map
    'hash-ordered-coll 'hash-set 'hash-unordered-coll 'ident? 'identical? 'identity 'ifn? 'inc
    'inc' 'indexed? 'inst-ms 'inst-ms* 'inst? 'instance? 'int 'int-array 'int? 'integer?
    'interleave 'interpose 'into 'into-array 'ints 'isa? 'iterate 'iterator-seq 'juxt 'keep
    'keep-indexed 'key 'keys 'keyword 'keyword? 'last 'list 'list* 'list? 'long 'long-array
    'longs 'make-array 'make-hierarchy 'map 'map-entry? 'map-indexed 'map? 'mapcat 'mapv 'max
    'max-key 'merge 'merge-with 'meta 'methods 'min 'min-key 'mix-collection-hash 'mod 'name
    'namespace 'nat-int? 'neg-int? 'neg? 'next 'nfirst 'nil? 'nnext 'not 'not-any? 'not-empty
    'not-every? 'not= 'nth 'nthnext 'nthrest 'num 'number? 'numerator 'object-array 'odd? 'or
    'parents 'partial 'partition 'partition-all 'partition-by 'peek 'pop 'pos-int? 'pos? 'print-str
    'println-str 'prn-str 'qualified-ident? 'qualified-keyword? 'qualified-symbol? 'quot 'rand
    'rand-int 'rand-nth 'random-sample 'range 'ratio? 'rational? 'rationalize 're-find 're-groups
    're-matcher 're-matches 're-pattern 're-seq 'reader-conditional 'reader-conditional?
    'realized? 'record? 'reduce 'reduce-kv 'reduced 'reduced? 'reductions 'rem 'remove 'repeat
    'repeatedly 'replace 'rest 'reverse 'reversible? 'rseq 'rsubseq 'run! 'satisfies? 'second
    'select-keys 'seq 'seq? 'seqable? 'sequence 'sequential? 'set 'set? 'short 'short-array 'shorts
    'shuffle 'simple-ident? 'simple-keyword? 'simple-symbol? 'slurp 'some 'some-fn 'some? 'sort
    'sort-by 'sorted-map 'sorted-map-by 'sorted-set 'sorted-set-by 'sorted? 'special-symbol?
    'split-at 'split-with 'str 'string? 'struct 'struct-map 'subs 'subseq 'subvec 'supers 'symbol
    'symbol? 'tagged-literal 'tagged-literal? 'take 'take-last 'take-nth 'take-while 'test
    'to-array 'to-array-2d 'transduce 'tree-seq 'true? 'type 'unchecked-add 'unchecked-add-int
    'unchecked-byte 'unchecked-char 'unchecked-dec 'unchecked-dec-int 'unchecked-divide-int
    'unchecked-double 'unchecked-float 'unchecked-inc 'unchecked-inc-int 'unchecked-int
    'unchecked-long 'unchecked-multiply 'unchecked-multiply-int 'unchecked-negate
    'unchecked-negate-int 'unchecked-remainder-int 'unchecked-short 'unchecked-subtract
    'unchecked-subtract-int 'underive 'unquote 'unquote-splicing 'unreduced
    'unsigned-bit-shift-right 'update 'update-in 'update-proxy 'uri? 'uuid? 'val 'vals 'vec
    'vector 'vector-of 'vector? 'with-meta 'xml-seq 'zero? 'zipmap})

(defn and*
  ([] true)
  ([x] x)
  ([x & n] (and x (apply and* n))))

(defn or*
  ([] nil)
  ([x] x)
  ([x & n] (or x (apply or* n))))

#?(:clj
   (def macro-fns {'and and* 'clojure.core/and and*
                   'or or* 'clojure.core/or or*}))

#?(:cljs
   (def allowed-map
     {'and and*, 'or or*,
      'sort-by cljs.core/sort-by, 'eduction cljs.core/eduction, 'tree-seq cljs.core/tree-seq,
      'unchecked-remainder-int cljs.core/unchecked-remainder-int, 'seq cljs.core/seq,
      'reduce cljs.core/reduce, 'contains? cljs.core/contains?, 'every? cljs.core/every?,
      'keep-indexed cljs.core/keep-indexed, 'subs cljs.core/subs, 'set cljs.core/set,
      'take-last cljs.core/take-last, 'bit-set cljs.core/bit-set,
      'qualified-keyword? cljs.core/qualified-keyword?, 'butlast cljs.core/butlast,
      'unchecked-subtract-int cljs.core/unchecked-subtract-int, 
      'take-nth cljs.core/take-nth, 'first cljs.core/first, 'seq? cljs.core/seq?,
      'println-str cljs.core/println-str, 'inst-ms cljs.core/inst-ms,
      'iterate cljs.core/iterate, 'fn? cljs.core/fn?, 'doall cljs.core/doall,
      'dedupe cljs.core/dedupe, 'dissoc cljs.core/dissoc,
      'bit-shift-right cljs.core/bit-shift-right, 'peek cljs.core/peek,
      'aget cljs.core/aget, 'last cljs.core/last, 'namespace cljs.core/namespace,
      '= cljs.core/=, 'take cljs.core/take, 'vector? cljs.core/vector?,
      'boolean cljs.core/boolean, 'bit-shift-left cljs.core/bit-shift-left,
      'any? cljs.core/any?, 'rand-int cljs.core/rand-int, 'dec cljs.core/dec, 
      'map cljs.core/map, 'juxt cljs.core/juxt, '< cljs.core/<, 'test cljs.core/test,
      'rest cljs.core/rest, 'isa? cljs.core/isa?, 'boolean? cljs.core/boolean?,
      're-seq cljs.core/re-seq, 'char? cljs.core/char?, 'make-hierarchy cljs.core/make-hierarchy,
      'keep cljs.core/keep, 'char cljs.core/char, 'mapcat cljs.core/mapcat,
      'unchecked-long cljs.core/unchecked-long, 'some? cljs.core/some?,
      'unchecked-negate cljs.core/unchecked-negate, 'reverse cljs.core/reverse, 'inst? cljs.core/inst?,
      'range cljs.core/range, 'sort cljs.core/sort, 'unchecked-inc-int cljs.core/unchecked-inc-int,
      'map-indexed cljs.core/map-indexed, 'rand-nth cljs.core/rand-nth, 'comp cljs.core/comp,
      'dorun cljs.core/dorun, 'simple-symbol? cljs.core/simple-symbol?, 'disj cljs.core/disj,
      'cons cljs.core/cons, 'floats cljs.core/floats, 'pos? cljs.core/pos?, 'fnil cljs.core/fnil,
      'merge-with cljs.core/merge-with, 'nthrest cljs.core/nthrest, 'sequential? cljs.core/sequential?,
      'shuffle cljs.core/shuffle, 'find cljs.core/find, 'alength cljs.core/alength,
      'bit-xor cljs.core/bit-xor, 'unsigned-bit-shift-right cljs.core/unsigned-bit-shift-right,
      'neg? cljs.core/neg?, 'unchecked-float cljs.core/unchecked-float, 'reduced? cljs.core/reduced?,
      'disj! cljs.core/disj!, 'float? cljs.core/float?, 'booleans cljs.core/booleans,
      'int-array cljs.core/int-array, 'set? cljs.core/set?, 'cat cljs.core/cat,
      'take-while cljs.core/take-while, '<= cljs.core/<=, 'conj! cljs.core/conj!,
      'repeatedly cljs.core/repeatedly, 'zipmap cljs.core/zipmap, 'remove cljs.core/remove,
      '* cljs.core/*, 're-pattern cljs.core/re-pattern, 'min cljs.core/min, 'prn-str cljs.core/prn-str,
      'reversible? cljs.core/reversible?, 'conj cljs.core/conj, 'transduce cljs.core/transduce,
      'compare-and-set! cljs.core/compare-and-set!, 'interleave cljs.core/interleave,
      'map? cljs.core/map?, 'get cljs.core/get, 'identity cljs.core/identity, 'into cljs.core/into,
      'long cljs.core/long, 'double cljs.core/double, 'nfirst cljs.core/nfirst, 'meta cljs.core/meta,
      'bit-and-not cljs.core/bit-and-not, 'unchecked-add-int cljs.core/unchecked-add-int,
      'hash-ordered-coll cljs.core/hash-ordered-coll, 'cycle cljs.core/cycle, 'empty? cljs.core/empty?,
      'short cljs.core/short, 'filterv cljs.core/filterv, 'hash cljs.core/hash, 'quot cljs.core/quot,
      'unchecked-double cljs.core/unchecked-double, 'key cljs.core/key, 'longs cljs.core/longs,
      'not= cljs.core/not=, 'string? cljs.core/string?, 'uri? cljs.core/uri?,
      'unchecked-multiply-int cljs.core/unchecked-multiply-int, 'double? cljs.core/double?,
      'vec cljs.core/vec, 'int cljs.core/int, 'map-entry? cljs.core/map-entry?, 'rand cljs.core/rand,
      'second cljs.core/second, '> cljs.core/>, 'replace cljs.core/replace, 'int? cljs.core/int?,
      'associative? cljs.core/associative?, 'unchecked-int cljs.core/unchecked-int,
      'inst-ms* cljs.core/inst-ms*, 'keyword? cljs.core/keyword?, 'force cljs.core/force,
      'group-by cljs.core/group-by, 'unchecked-multiply cljs.core/unchecked-multiply,
      'even? cljs.core/even?, 'unchecked-dec cljs.core/unchecked-dec,
      'tagged-literal? cljs.core/tagged-literal?, 'double-array cljs.core/double-array,
      'rseq cljs.core/rseq, 'float cljs.core/float, 'concat cljs.core/concat, 'symbol cljs.core/symbol,
      'to-array-2d cljs.core/to-array-2d, 'mod cljs.core/mod, 'pop cljs.core/pop,
      'dissoc! cljs.core/dissoc!, 'reductions cljs.core/reductions, 'indexed? cljs.core/indexed?,
      '- cljs.core/-, 'assoc! cljs.core/assoc!, 'hash-set cljs.core/hash-set,
      'reduce-kv cljs.core/reduce-kv, 'name cljs.core/name, 'ffirst cljs.core/ffirst,
      'sorted-set cljs.core/sorted-set, 'counted? cljs.core/counted?,
      'tagged-literal cljs.core/tagged-literal, 'assoc-in cljs.core/assoc-in, 'bit-test cljs.core/bit-test,
      'zero? cljs.core/zero?, 'simple-keyword? cljs.core/simple-keyword?,
      'unchecked-dec-int cljs.core/unchecked-dec-int, 'nnext cljs.core/nnext,
      'not-every? cljs.core/not-every?, 'rem cljs.core/rem, 'some cljs.core/some,
      'neg-int? cljs.core/neg-int?, 'drop cljs.core/drop, 'nth cljs.core/nth, 'sorted? cljs.core/sorted?,
      'nil? cljs.core/nil?, 'split-at cljs.core/split-at, 'random-sample cljs.core/random-sample,
      'select-keys cljs.core/select-keys, 'bit-and cljs.core/bit-and,
      'bounded-count cljs.core/bounded-count, 'update cljs.core/update, 'list* cljs.core/list*,
      'update-in cljs.core/update-in, 'ensure-reduced cljs.core/ensure-reduced,
      'instance? cljs.core/instance?, 'mix-collection-hash cljs.core/mix-collection-hash,
      're-find cljs.core/re-find, 'run! cljs.core/run!, 'val cljs.core/val,
      'unchecked-add cljs.core/unchecked-add, 'not cljs.core/not, 'with-meta cljs.core/with-meta,
      'unreduced cljs.core/unreduced, 'record? cljs.core/record?, 'type cljs.core/type,
      'identical? cljs.core/identical?, 'unchecked-divide-int cljs.core/unchecked-divide-int,
      'max-key cljs.core/max-key, 'ident? cljs.core/ident?, 'vals cljs.core/vals,
      'unchecked-subtract cljs.core/unchecked-subtract, 'sorted-set-by cljs.core/sorted-set-by,
      'qualified-ident? cljs.core/qualified-ident?, 'true? cljs.core/true?, 'empty cljs.core/empty,
      '/ cljs.core//, 'bit-or cljs.core/bit-or, 'vector cljs.core/vector, '>= cljs.core/>=,
      'drop-last cljs.core/drop-last, 'not-empty cljs.core/not-empty, 'distinct cljs.core/distinct,
      'partition cljs.core/partition, 'bit-flip cljs.core/bit-flip, 'long-array cljs.core/long-array,
      'descendants cljs.core/descendants, 'merge cljs.core/merge, 'integer? cljs.core/integer?,
      'mapv cljs.core/mapv, 'partition-all cljs.core/partition-all, 'partition-by cljs.core/partition-by,
      'object-array cljs.core/object-array, 'derive cljs.core/derive,
      'special-symbol? cljs.core/special-symbol?, 'subseq cljs.core/subseq, 'gensym cljs.core/gensym,
      'delay? cljs.core/delay?, 'flatten cljs.core/flatten, 'doubles cljs.core/doubles,
      'halt-when cljs.core/halt-when, 'ifn? cljs.core/ifn?, 'nat-int? cljs.core/nat-int?,
      'subvec cljs.core/subvec, 'partial cljs.core/partial, 'min-key cljs.core/min-key,
      'reduced cljs.core/reduced, 're-matches cljs.core/re-matches, 'array-map cljs.core/array-map,
      'unchecked-byte cljs.core/unchecked-byte, 'every-pred cljs.core/every-pred, 'keys cljs.core/keys,
      'distinct? cljs.core/distinct?, 'pos-int? cljs.core/pos-int?,
      'unchecked-short cljs.core/unchecked-short, 'methods cljs.core/methods, 'odd? cljs.core/odd?,
      'frequencies cljs.core/frequencies, 'rsubseq cljs.core/rsubseq, 'inc cljs.core/inc,
      'uuid? cljs.core/uuid?, 'bit-clear cljs.core/bit-clear, 'filter cljs.core/filter,
      'list cljs.core/list, '+ cljs.core/+, 'split-with cljs.core/split-with, 'aset cljs.core/aset,
      'keyword cljs.core/keyword, 'chars cljs.core/chars, 'str cljs.core/str, 'next cljs.core/next,
      'hash-map cljs.core/hash-map, 'underive cljs.core/underive, 'false? cljs.core/false?,
      'ints cljs.core/ints, 'some-fn cljs.core/some-fn, 'to-array cljs.core/to-array,
      'list? cljs.core/list?, 'simple-ident? cljs.core/simple-ident?, 'bit-not cljs.core/bit-not,
      'byte cljs.core/byte, 'max cljs.core/max, '== cljs.core/==, 'parents cljs.core/parents,
      'count cljs.core/count, 'sorted-map-by cljs.core/sorted-map-by, 'apply cljs.core/apply,
      'interpose cljs.core/interpose, 'deref cljs.core/deref, 'assoc cljs.core/assoc,
      'comparator cljs.core/comparator, 'sorted-map cljs.core/sorted-map,
      'drop-while cljs.core/drop-while, 'realized? cljs.core/realized?, 'compare cljs.core/compare,
      'complement cljs.core/complement, 'sequence cljs.core/sequence, 'constantly cljs.core/constantly,
      'make-array cljs.core/make-array, 'shorts cljs.core/shorts, 'completing cljs.core/completing,
      'unchecked-negate-int cljs.core/unchecked-negate-int,
      'hash-unordered-coll cljs.core/hash-unordered-coll, 'repeat cljs.core/repeat,
      'unchecked-inc cljs.core/unchecked-inc, 'nthnext cljs.core/nthnext, 'number? cljs.core/number?,
      'print-str cljs.core/print-str, 'not-any? cljs.core/not-any?, 'into-array cljs.core/into-array,
      'qualified-symbol? cljs.core/qualified-symbol?, 'seqable? cljs.core/seqable?,
      'symbol? cljs.core/symbol?, 'unchecked-char cljs.core/unchecked-char, 'coll? cljs.core/coll?,
      'get-in cljs.core/get-in, 'fnext cljs.core/fnext, 'bytes cljs.core/bytes}))

#?(:cljs
   (def allowed-map-str
     {'ends-with? clojure.string/ends-with?, 'capitalize clojure.string/capitalize,
      'reverse clojure.string/reverse, 'join clojure.string/join,
      'replace-first clojure.string/replace-first, 'starts-with? clojure.string/starts-with?,
      'escape clojure.string/escape, 'last-index-of clojure.string/last-index-of,
      'includes? clojure.string/includes?, 'replace clojure.string/replace,
      'split-lines clojure.string/split-lines, 'lower-case clojure.string/lower-case,
      'trim-newline clojure.string/trim-newline, 'upper-case clojure.string/upper-case,
      'split clojure.string/split, 'trimr clojure.string/trimr, 'index-of clojure.string/index-of,
      'trim clojure.string/trim, 'triml clojure.string/triml, 'blank? clojure.string/blank?}))
